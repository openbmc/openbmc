SUMMARY = "Add default Users"
DESCRIPTION = "Add Users"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

EXCLUDE_FROM_WORLD = "1"

DEPENDS = "bmcweb"
DEPENDS += "phosphor-ipmi-host"
DEPENDS += "phosphor-user-manager"
RDEPENDS:${PN} = "bmcweb"
RDEPENDS:${PN} += "phosphor-ipmi-host"
RDEPENDS:${PN} += "phosphor-user-manager"

inherit useradd
USERADD_PACKAGES = "${PN}"

USERADD_PARAM:${PN} = "-m -N -u 1000 -g 100 -s /bin/nologin \
                       -G 'web,redfish,priv-admin' Megapede; "
GROUPMEMS_PARAM:${PN} = "-g priv-admin -a root; "
GROUPMEMS_PARAM:${PN} += "-g ipmi -a root; "

ALLOW_EMPTY:${PN} = "1"
