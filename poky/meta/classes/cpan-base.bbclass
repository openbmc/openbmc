#
# cpan-base providers various perl related information needed for building
# cpan modules
#
FILES_${PN} += "${libdir}/perl ${datadir}/perl"

DEPENDS  += "${@["perl", "perl-native"][(bb.data.inherits_class('native', d))]}"
RDEPENDS_${PN} += "${@["perl", ""][(bb.data.inherits_class('native', d))]}"

inherit perl-version

def is_target(d):
    if not bb.data.inherits_class('native', d):
        return "yes"
    return "no"

PERLLIBDIRS = "${libdir}/perl"
PERLLIBDIRS_class-native = "${libdir}/perl-native"
