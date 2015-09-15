DEFAULT_PREFERENCE = "-1"

include gstreamer1.0-plugins-base.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=c54ce9345727175ff66d17b67ff51f58 \
                    file://common/coverage/coverage-report.pl;beginline=2;endline=17;md5=a4e1830fce078028c8f0974161272607 \
                    file://COPYING.LIB;md5=6762ed442b3822387a51c92d928ead0d \
                   "

S = "${WORKDIR}/git"

SRCREV = "8d4cb64a4b9d84b10076bf350f80a0d6ea68ec2d"

do_configure_prepend() {
	cd ${S}
	./autogen.sh --noconfigure
	cd ${B}
}

