SUMMARY = "FS update Framework azure"
LICENSE = "CLOSED"

S = "${WORKDIR}/fs_updater_azure"

DEPENDS = " \
	libubootenv \
	botan \
	jsoncpp \
	zlib \
	inicpp \
	fs-updater-lib \
	tclap \
"

SRC_URI = "file://fs_updater_azure.tar.gz"

inherit cmake
