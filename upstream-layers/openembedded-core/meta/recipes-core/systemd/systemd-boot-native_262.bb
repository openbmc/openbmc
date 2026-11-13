require systemd.inc
FILESEXTRAPATHS =. "${FILE_DIRNAME}/systemd:"

inherit native

deltask do_configure
deltask do_compile

do_install () {
       install -Dm 0755 ${S}/src/ukify/ukify.py ${D}${bindir}/ukify
}
addtask install after do_patch

PACKAGES = "${PN}"

FILES:${PN} = "${bindir}/ukify"

RDEPENDS:${PN} += "python3-pefile-native"
