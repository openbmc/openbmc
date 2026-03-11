SUMMARY = "Sysroot poisoning test"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

LICENSE = "MIT"

inherit nopackages

# This test confirms that compiling code that searches /usr/include for headers
# will result in compiler errors.  This recipe should will fail to build and
# oe-selftest has a test that verifies that.
python do_compile() {
    import subprocess

    tests = {
        "Preprocessor": "${CPP} -I/usr/include -",
        "C Compiler": "${CC} -I/usr/include -x c -",
        "C++ Compiler": "${CXX} -I/usr/include -x c++ -",
    }

    for name, cmd in tests.items():
        cmd = d.expand(cmd)
        bb.note("Test command: " + cmd)
        testcode = "int main(int argc, char** argv) {}"
        proc = subprocess.run(cmd, shell=True, input=testcode, capture_output=True, text=True)

        if proc.returncode != 0 and "is unsafe for cross-compilation" in proc.stderr:
            bb.note(f"{name} passed: {proc.stderr}")
        else:
            bb.error(f"{name} is not poisoned. Exit status {proc.returncode}, output: {proc.stdout} {proc.stderr}")
}

EXCLUDE_FROM_WORLD = "1"
