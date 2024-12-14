SUMMARY = "New user to do specific job"
DESCRIPTION = "This recipe create a new user named ${PN}, who is used for specific jobs like building. The task can be auto started via mini X"
SECTION = "x11"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://builder_session.sh;endline=5;md5=84796c3c41785d86100fdabcbdade00e"

SRC_URI = "file://builder_session.sh \
          "

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

RDEPENDS:${PN} = "mini-x-session"

inherit useradd

# builder user password is "builder"
BUILDER_PASSWORD ?= ".gLibiNXn0P12"
USERADD_PACKAGES = "${PN}"
USERADD_PARAM:${PN} = "--system --create-home \
                       --groups video,tty,audio \
                       --password ${BUILDER_PASSWORD} \
                       --user-group builder"

do_install () {
	install -d -m 755 ${D}${sysconfdir}/mini_x/session.d
	install -p -m 755 builder_session.sh ${D}${sysconfdir}/mini_x/session.d/

	chown  builder.builder ${D}${sysconfdir}/mini_x/session.d/builder_session.sh
}

# do not report CVEs for other builder apps
CVE_PRODUCT = "yoctoproject:builder"
