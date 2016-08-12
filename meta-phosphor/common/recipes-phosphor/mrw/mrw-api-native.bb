SUMMARY = "Phosphor OpenBMC machine readable workbook API modules"
DESCRIPTION = "The API for the MRW XML generated by the Serverwiz tool"
PR = "r1"

S = "${WORKDIR}/scripts"

inherit obmc-phosphor-license
inherit native
inherit perlnative
inherit cpan-base


SRC_URI += "git://github.com/open-power/serverwiz.git;subpath=scripts"
SRCREV = "04f15f37e9ab6c09412a0abcffb87e9aefcc5368"

do_install() {
    install -d ${PERLLIBDIRS_class-native}/perl/site_perl/${PERLVERSION}/mrw
    install -m 0755 Targets.pm ${PERLLIBDIRS_class-native}/perl/site_perl/${PERLVERSION}/mrw/Targets.pm
}
