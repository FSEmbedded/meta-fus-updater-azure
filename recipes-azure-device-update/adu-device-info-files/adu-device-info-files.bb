# Generates a text file with the ADU applicability info
# for manufacturer and model and copies/installs that file into the image.

# Environment variables that can be used to configure the behaviour of this recipe.
# MANUFACTURER          The manufacturer string that will be written to the manufacturer
#                       file and reported through the Device Information PnP Interface.
# MODEL                 The model string that wil be written to the model file and
#                       reported through the Device Information PnP Interface.
# ADU_SOFTWARE_VERSION  The software version for the image/firmware. Will be written to
#                       the version file that is read by ADU Client.

LICENSE="CLOSED"

# Generate the manufacturer, model, and version files
do_compile() {
    echo "aduc_manufacturer = aduc_${MANUFACTURER}" > adu-conf.txt
    echo "aduc_model = aduc_${MODEL}" >> adu-conf.txt
    echo "manufacturer = ${MANUFACTURER}" >> adu-conf.txt
    echo "model = ${MODEL}" >> adu-conf.txt
}

# Install the files on the image in /etc
do_install() {
    install -d ${D}"/adu"
    install -m ugo=rw adu-conf.txt ${D}/adu/adu-conf.txt
}

FILES_${PN} += "/adu/adu-conf.txt"

inherit allarch
