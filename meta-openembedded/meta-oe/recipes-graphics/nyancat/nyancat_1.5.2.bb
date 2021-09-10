SUMMARY = "Render a loop of the Nyan Cat / Poptart Cat animation"
HOMEPAGE = "https://nyancat.dakko.us/"
SECTION = "graphics"

LICENSE = "NCSA"
LIC_FILES_CHKSUM = "file://src/nyancat.c;beginline=27;endline=49;md5=285f7ac87da4a631f348800687d845bd"

S = "${WORKDIR}/git"

SRCREV = "5ffb6c5c03d0e9156db8f360599d4f0449bb16b9"
SRC_URI = " \
	git://github.com/klange/nyancat;protocol=https;branch=master \
"

do_install:append() {
	install -Dm 0755 ${S}/src/${BPN} ${D}${bindir}/${BPN}
}
