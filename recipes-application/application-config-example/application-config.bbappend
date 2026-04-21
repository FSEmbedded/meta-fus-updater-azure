FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
SRC_URI = "\
	file://overlay.ini \
"
DESCRIPTION = "Extended overlay.ini for Azure Device Update. \
Adds write access to /etc for ADU configuration."
