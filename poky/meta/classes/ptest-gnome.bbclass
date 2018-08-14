inherit ptest

EXTRA_OECONF_append = " ${@bb.utils.contains('PTEST_ENABLED', '1', '--enable-installed-tests', '--disable-installed-tests', d)}"

FILES_${PN}-ptest += "${libexecdir}/installed-tests/ \
                      ${datadir}/installed-tests/"

RDEPENDS_${PN}-ptest += "gnome-desktop-testing"
