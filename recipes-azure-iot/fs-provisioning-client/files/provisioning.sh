#!/bin/bash

#set -x

# Global variables
DEVICE_PATH="devices"
CUSTOMER_PATH=""
CRT_PATH="x509"
device_id=""
id_scope=""
iot_hub=""
HOME_DIR=$(pwd)

check_parameters()
{
	if [ -z $prepare ];
	then
		usage
	elif [ -z $prov ] || [ -z $id_scope ] || [ -z $iot_hub ] || [ -z $device_id ] || [ $DEVICE_PATH == "devices" ];
	then
		usage
		exit 1
	fi
}

prepare_dir_for_customer()
{
	if [ -d $CUSTOMER_PATH ];
	then
		echo "Customer already exist, abort!"
	else
		mkdir -p $CUSTOMER_PATH
	fi

	if [ ! -f $CUSTOMER_PATH/certGen.sh ];
	then
		cp $HOME_DIR/$CRT_PATH/certGen.sh $CUSTOMER_PATH
	fi

	if [ ! -f $CUSTOMER_PATH/openssl_device_intermediate_ca.cnf ];
	then
		cp $HOME_DIR/$CRT_PATH/openssl_device_intermediate_ca.cnf $CUSTOMER_PATH
	fi

	if [ ! -f $CUSTOMER_PATH/openssl_root_ca.cnf ];
	then
		cp $HOME_DIR/$CRT_PATH/openssl_root_ca.cnf $CUSTOMER_PATH
	fi
}

# CREATE DEST-DIR
create_dest_dir()
{
	if [ -d $CUSTOMER_PATH/$DEVICE_PATH ];
	then
		echo "Device already exist, done!"
	else
		mkdir -p $CUSTOMER_PATH/$DEVICE_PATH
	fi
}

# CREATE DEVICE IDENTITY TXT
create_dev_id()
{
	if [ -f $CUSTOMER_PATH/$DEVICE_PATH/$device_id.txt ];
	then
		echo "$device_id.txt exist, done!"
	else
		echo -n $device_id > $CUSTOMER_PATH/$DEVICE_PATH/$device_id.txt
	fi
}

# GENERATE ROOT AND INTERMEDIATE CA
create_ca()
{
	echo "Create CA ..."
	if [ -d $CUSTOMER_PATH/certs/ ];
	then
		echo "CA already exist!"
	else
		cd $CUSTOMER_PATH
		TERM=xterm ./certGen.sh create_root_and_intermediate
                #./certGen.sh create_ca_certificate
		if [ "$?" != "0" ]; then
			printf "\ncertGen.sh error! Aborting ....\n\n"
			cd ..
			exit 1
		fi
		cd ..
	fi
}

# If name exists, delete it!
check_name()
{
    sed -i "/$device_id/d" "$CUSTOMER_PATH/index.txt"
}

# generate device directory, certificates and copy certificates to
# device directory
create_dev_certs()
{
	check_name
	#check if device dir and device certificates exist
	if [ -d $CUSTOMER_PATH/$DEVICE_PATH/x509_c ];
	then
		if [ -f $CUSTOMER_PATH/$DEVICE_PATH/x509_c/$device_id.cert.pem ] && \
			[ -f $CUSTOMER_PATH/$DEVICE_PATH/x509_c/$device_id.cert.pem ];
		then
			echo "Device dirictory and certificats exist, done!"
		 	return 2
		fi
	fi
	# otherwise create device directory and certificates
	cd $CUSTOMER_PATH
	TERM=xterm ./certGen.sh create_device_certificate $device_id
	if [ "$?" != "0" ]; then
		printf "\ncertGen.sh error! Aborting ....\n\n"
		rm csr/$device_id.csr.pem
		rm -f private/$device_id.key.pem
		cd ..
		exit 1
	fi
	# move certs to device dir...
	move_dev_certs_to_dest_dir
}

move_dev_certs_to_dest_dir()
{
	for DEVICE_PATH in $DEVICE_PATH
	do
		mkdir -p $CUSTOMER_PATH/$DEVICE_PATH/x509_c
		cp $CUSTOMER_PATH/certs/$device_id.cert.pem $CUSTOMER_PATH/$DEVICE_PATH/x509_c
		cp $CUSTOMER_PATH/private/$device_id.key.pem $CUSTOMER_PATH/$DEVICE_PATH/x509_c
	done
}

delete_dev_certs()
{
	if [ -f $CUSTOMER_PATH/certs/$device_id.cert.pem ];
	then
		rm -f $CUSTOMER_PATH/certs/$device_id.cert.pem
	fi

	if [ -f $CUSTOMER_PATH/certs/$device_id.cert.pfx ];
	then
		rm $CUSTOMER_PATH/certs/$device_id.cert.pfx
	fi

	if [ -f $CUSTOMER_PATH/private/$device_id.key.pem ];
	then
		rm -f $CUSTOMER_PATH/private/$device_id.key.pem
	fi

	if [ -f $CUSTOMER_PATH/csr/$device_id.csr.pem ];
	then
		rm $CUSTOMER_PATH/csr/$device_id.csr.pem
	fi
}

add_device_twin_to_iothub()
{
	for DEVICE_PATH in $DEVICE_PATH
	do
		cp $HOME_DIR/fus_prov_dps_client $CUSTOMER_PATH/$DEVICE_PATH
		cd $CUSTOMER_PATH/$DEVICE_PATH/
		./fus_prov_dps_client $id_scope $device_id.txt \
							  ./x509_c/$device_id.cert.pem \
							  ./x509_c/$device_id.key.pem
		retval=$?
		rm fus_prov_dps_client
	done
	return $retval
}

pack_certs()
{
	cd $CUSTOMER_PATH/$DEVICE_PATH/
	tar cfvj certs.tar.bz2 du-config.json x509_c/$device_id.cert.pem x509_c/$device_id.key.pem
}

addfsheader()
{
		cp $HOME_DIR/addfsheader.sh $CUSTOMER_PATH/$DEVICE_PATH
		cd $CUSTOMER_PATH/$DEVICE_PATH/
		./addfsheader.sh -t CERT certs.tar.bz2 > certs.fs
		rm addfsheader.sh
}

azure_login()
{
	# no longer works, because multi-factor authentification is needed!
	read -p "Azure Login-ID: " AZ_ID && read -sp "Azure Password: " AZ_PASS && echo && az login -u $AZ_ID -p $AZ_PASS
	# TODO:
	# use service-principal for login to Azure Cloud
}

azure_logout()
{
	az logout
}

patch_twin()
{
	az iot hub device-twin update -n $iot_hub -d $device_id --tags "{"$attribute": "$value"}"
}

du_conf()
{
	# copy template-du-config.json to specific device directory
	cp -rf $HOME_DIR/template-du-config.json $CUSTOMER_PATH/$DEVICE_PATH/du-config.json
	# replace place holder with configured strings
	sed -i "s/<x509_store>/\"\/adu\/x509_c\"/g" $CUSTOMER_PATH/$DEVICE_PATH/du-config.json
	sed -i "s/<x509_cert>/\""${device_id}".cert.pem\"/g" $CUSTOMER_PATH/$DEVICE_PATH/du-config.json
	sed -i "s/<x509_key>/\""${device_id}".key.pem\"/g" $CUSTOMER_PATH/$DEVICE_PATH/du-config.json
	sed -i "s/<device_id>/\"${device_id}\"/g" $CUSTOMER_PATH/$DEVICE_PATH/du-config.json
	sed -i "s/<iothub_name>/\"${iot_hub}\"/g" $CUSTOMER_PATH/$DEVICE_PATH/du-config.json
	sed -i "s/<iothub_suffix>/\"azure-devices.net\"/g" $CUSTOMER_PATH/$DEVICE_PATH/du-config.json
	sed -i "s/<connection_type>/\"x509\"/g" $CUSTOMER_PATH/$DEVICE_PATH/du-config.json
	sed -i "s/<connection_data>/\"\"/g" $CUSTOMER_PATH/$DEVICE_PATH/du-config.json
	sed -i "s/<name>/\"fus\/update\"/g" $CUSTOMER_PATH/$DEVICE_PATH/du-config.json
	sed -i "s/<manufacturer>/\"${MANIFEST_PROVIDER}\"/g" $CUSTOMER_PATH/$DEVICE_PATH/du-config.json
	sed -i "s/<model>/\"${MANIFEST_DEVICE_MOD}\"/g" $CUSTOMER_PATH/$DEVICE_PATH/du-config.json
}

main()
{
	create_dest_dir
	create_dev_id
	if [ -f $CUSTOMER_PATH/certs/$device_id.cert.pem ] && [ -f $CUSTOMER_PATH/private/$device_id.key.pem ];
	then
		echo "Device certificates already exist!"
		delete_dev_certs
		echo "Successful delete device certificates."
	fi
	create_dev_certs
	dev_available=$?
	delete_dev_certs
	du_conf
	add_device_twin_to_iothub
	if [ $? == 5 ];
	then
		echo "Device $device_id can not be created on Azure."
		echo "Communication to DSP fails. E.g. check if root cert is available on Azure."
	fi

	if [ $dev_available == 2 ];
	then
		echo "Device artifacts are available, done!"
		return 0
	fi

	pack_certs
	addfsheader
	#patch_twin
}

usage()
{
	printf "\n
	Usage: 	provisioning.sh -h || --help
		provisioning.sh -g || --prepare && --customer <path/and/name/to/customer/dir>
		provisioning.sh -n || --prov	&& --customer <path/and/name/to/customer/dir>
						   --crtdir <serial number>
						   --devid <serial number>
						   --idscope <ID-Scope of DPS>
						   --iothub <IoT Hub name>
	\n
		 -g, --prepare:		create CA (setting the parameter --customer is additionally required!)\n
		 -n, --prov:		parameter for provisioning prozess (all of the following parameters must also be set!)\n
		--customer:		path and name of customer directory\n
		--devdir:  		name of destination directory for device in customer directory\n
		--devid:   		name of device twin in IOT Hub and common name of the device certificate\n
		--idscope: 		id of DPS\n
		--iothub		name of IoT Hub to set connection_string in adu-conf.txt

		<OPTIONAL>
		 -h, --help:		display this help page\n
		 -l, --azlogin:		Azure login\n
		 -o, --azlogout:	Azure loggout\n
	\n"
}

OPTS=`getopt -n 'provisioning'  -o hgnlo --long help,azlogin,azlogout,customer:,devdir:,devid:,idscope:,iothub:,tag_attribute:,tag_value:,home_dir: -- "$@"`
eval set -- "$OPTS"
while true
do
	case "$1" in
	-h | --help )
		help=1
		shift 1
		;;
	-g )
		prepare=1
		shift 1
		if [ $# != 5 ]; then
			echo "ERROR: Invalid argc"
			usage
			exit
		fi
		;;
	-n )
		prov=1
		shift 1
		;;
	--customer )
		CUSTOMER_PATH="$CUSTOMER_PATH/$2"
		shift 2
		;;
	--devdir )
		DEVICE_PATH="$DEVICE_PATH/$2"
		shift 2
		;;
	--devid )
		device_id=$2
		shift 2
		;;
	--idscope )
		id_scope=$2
		shift 2
		;;
	--iothub )
		iot_hub=$2
		shift 2
		;;
	--tag_attribute )
		attribute=$2
		shift 2
		;;
	--tag_value )
		value=$2
		shift 2
		;;
	--home_dir )
		HOME_DIR=$2
		shift 2
		;;
	-l | --azlogin )
		log_in=1
		shift 1
		;;
	-o | --azlogout )
		log_out=1
		shift 1
		;;
	* )	break
	    ;;
   esac
done

if [ -n "$help" ];
then
	usage
	exit
elif [ -n "$prepare" ];
then
	prepare_dir_for_customer
	create_ca
elif [ -n "$prov" ] && [ -n "$id_scope" ] && [ -n "$iot_hub" ] && [ -n "$device_id" ];
then
	echo "create dev certs and register dev to IoT-Hub ..."
	if [ -n "$log_in" ];
	then
		azure_login
		main
		exit
	elif [ -n "$log_out" ];
	then
		main
		azure_logout
		exit
	else
		main
	fi
else
	usage
fi

echo "Finish, device $device_id is created..."
