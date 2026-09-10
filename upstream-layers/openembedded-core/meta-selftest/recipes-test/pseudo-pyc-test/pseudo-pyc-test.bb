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

TINFOIL_TEST_MARKER_DIR = "${TMPDIR}/tinfoil-prepared-task"
TINFOIL_TEST_MARKER_VALUE ?= ""

write_tinfoil_shell_marker() {
    mkdir -p ${TINFOIL_TEST_MARKER_DIR}
    printf '%s%s' "$1" "${TINFOIL_TEST_MARKER_VALUE}" > ${TINFOIL_TEST_MARKER_DIR}/"$1"
}

do_tinfoil_dep() {
    write_tinfoil_shell_marker dep
}
addtask tinfoil_dep

do_tinfoil_shell() {
    write_tinfoil_shell_marker shell
}
addtask tinfoil_shell after do_tinfoil_dep

do_tinfoil_shell_fakeroot() {
    write_tinfoil_shell_marker shell-fakeroot
}
do_tinfoil_shell_fakeroot[fakeroot] = "1"
do_tinfoil_shell_fakeroot[depends] += "virtual/fakeroot-native:do_populate_sysroot"
addtask tinfoil_shell_fakeroot after do_tinfoil_dep

def write_tinfoil_marker(d, name):
    import os

    marker_dir = d.getVar('TINFOIL_TEST_MARKER_DIR')
    os.makedirs(marker_dir, exist_ok=True)
    with open(os.path.join(marker_dir, name), 'w') as marker:
        marker.write(name + d.getVar('TINFOIL_TEST_MARKER_VALUE'))

python do_tinfoil_python() {
    write_tinfoil_marker(d, 'python')
}
addtask tinfoil_python after do_tinfoil_dep

python do_tinfoil_python_fakeroot() {
    write_tinfoil_marker(d, 'python-fakeroot')
}
do_tinfoil_python_fakeroot[fakeroot] = "1"
do_tinfoil_python_fakeroot[depends] += "virtual/fakeroot-native:do_populate_sysroot"
addtask tinfoil_python_fakeroot after do_tinfoil_dep
