SUMMARY = "Phosphor LED Group Management meta data"
PR = "r1"

inherit obmc-phosphor-utils
inherit obmc-phosphor-license

# Choose appropriate yaml file
def get_depends(d):
    if d.getVar('USE_MRW', 'yes'):
        return "${PN}-mrw"
    else:
        return "${PN}-example"

USE_MRW = "${@cf_enabled('obmc-mrw', 'yes', d)}"
DEPENDS += "${@get_depends(d)}"
