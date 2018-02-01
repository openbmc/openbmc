require fakeroot_${PV}.bb

S = "${WORKDIR}/fakeroot-${PV}"

inherit native

EXTRA_OECONF = "--program-prefix="

# Compatability for the rare systems not using or having SYSV
python () {
    if d.getVar('HOST_NONSYSV') and d.getVar('HOST_NONSYSV') != '0':
        d.setVar('EXTRA_OECONF', ' --with-ipc=tcp --program-prefix= ')
}

RDEPENDS_${PN} = "util-linux-native"
