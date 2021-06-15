SUMMARY = "pseudo env test"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

INHIBIT_DEFAULT_DEPS = "1"

python do_compile() {
    import pseudo_pyc_test1
    print(pseudo_pyc_test1.STRING)
}

python do_install() {
    import pseudo_pyc_test2
    print(pseudo_pyc_test2.STRING)
}
