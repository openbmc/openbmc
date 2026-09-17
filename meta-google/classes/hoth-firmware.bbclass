SUMMARY = "Hoth firmware bundle"
DESCRIPTION = "Hoth firmware bundle"

LICENSE = "CLOSED"

# Support BMC image to have secondary hoth firmware
ENABLE_HOTH_SECONDARY ?= "no"
CAN_BE_SECONDARY ?= "no"
SUFFIX = "${@'-2nd' if d.getVar('ENABLE_HOTH_SECONDARY') == 'yes' and d.getVar('CAN_BE_SECONDARY') == 'yes' else ''}"

# Use a dummy SRC_URI. The source will be fetched from the mirror instead.
SRC_URI = "http://dummy/${FILENAME}"

PROVIDES += "virtual/hoth-firmware${SUFFIX}"

S = "${UNPACKDIR}"

inherit deploy

do_deploy () {
    install -D -m 0644 "${UNPACKDIR}/${FILENAME}" "${DEPLOYDIR}/image-hoth-update${SUFFIX}"
}
do_deploy[vardeps] += "FILENAME SUFFIX"

addtask deploy before do_build after do_compile
