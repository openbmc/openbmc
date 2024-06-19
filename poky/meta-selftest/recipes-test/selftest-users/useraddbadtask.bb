SUMMARY = "UserAddBadTask"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

LICENSE = "MIT"

DEPENDS:append = "coreutils-native"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

inherit useradd allarch

USERADD_PACKAGES = "${PN}"
USERADD_PARAM:${PN} = "-u 5555 --gid groupaddtask useraddtask"
GROUPADD_PARAM:${PN} = "-r groupaddtask"

do_badthingshappen() {
 echo "foo"
}

addtask badthingshappen after do_populate_sysroot before do_package
