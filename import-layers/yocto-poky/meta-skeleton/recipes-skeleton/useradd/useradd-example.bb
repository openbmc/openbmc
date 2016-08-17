SUMMARY = "Example recipe for using inherit useradd"
DESCRIPTION = "This recipe serves as an example for using features from useradd.bbclass"
SECTION = "examples"
PR = "r1"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/LICENSE;md5=4d92cd373abda3937c2bc47fbc49d690 \
                    file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

SRC_URI = "file://file1 \
           file://file2 \
           file://file3 \
           file://file4"

S = "${WORKDIR}"

PACKAGES =+ "${PN}-user3"

inherit useradd

# You must set USERADD_PACKAGES when you inherit useradd. This
# lists which output packages will include the user/group
# creation code.
USERADD_PACKAGES = "${PN} ${PN}-user3"

# You must also set USERADD_PARAM and/or GROUPADD_PARAM when
# you inherit useradd.

# USERADD_PARAM specifies command line options to pass to the
# useradd command. Multiple users can be created by separating
# the commands with a semicolon. Here we'll create two users,
# user1 and user2:
USERADD_PARAM_${PN} = "-u 1200 -d /home/user1 -r -s /bin/bash user1; -u 1201 -d /home/user2 -r -s /bin/bash user2"

# user3 will be managed in the useradd-example-user3 pacakge:
# As an example, we use the -P option to set clear text password for user3
USERADD_PARAM_${PN}-user3 = "-u 1202 -d /home/user3 -r -s /bin/bash -P 'user3' user3"

# GROUPADD_PARAM works the same way, which you set to the options
# you'd normally pass to the groupadd command. This will create
# groups group1 and group2:
GROUPADD_PARAM_${PN} = "-g 880 group1; -g 890 group2"

# Likewise, we'll manage group3 in the useradd-example-user3 package:
GROUPADD_PARAM_${PN}-user3 = "-g 900 group3"

do_install () {
	install -d -m 755 ${D}${datadir}/user1
	install -d -m 755 ${D}${datadir}/user2
	install -d -m 755 ${D}${datadir}/user3

	install -p -m 644 file1 ${D}${datadir}/user1/
	install -p -m 644 file2 ${D}${datadir}/user1/

	install -p -m 644 file2 ${D}${datadir}/user2/
	install -p -m 644 file3 ${D}${datadir}/user2/

	install -p -m 644 file3 ${D}${datadir}/user3/
	install -p -m 644 file4 ${D}${datadir}/user3/

	# The new users and groups are created before the do_install
	# step, so you are now free to make use of them:
	chown -R user1 ${D}${datadir}/user1
	chown -R user2 ${D}${datadir}/user2
	chown -R user3 ${D}${datadir}/user3

	chgrp -R group1 ${D}${datadir}/user1
	chgrp -R group2 ${D}${datadir}/user2
	chgrp -R group3 ${D}${datadir}/user3
}

FILES_${PN} = "${datadir}/user1/* ${datadir}/user2/*"
FILES_${PN}-user3 = "${datadir}/user3/*"

# Prevents do_package failures with:
# debugsources.list: No such file or directory:
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
