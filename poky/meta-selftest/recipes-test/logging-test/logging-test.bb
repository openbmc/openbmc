SUMMARY = "Destined to fail"
LICENSE = "CLOSED"

deltask do_patch
INHIBIT_DEFAULT_DEPS = "1"

do_shelltest() {
        echo "This is shell stdout"
        echo "This is shell stderr" >&2
        exit 1
}
addtask do_shelltest

python do_pythontest_exit () {
    print("This is python stdout")
    sys.exit(1)
}
addtask do_pythontest_exit

python do_pythontest_fatal () {
    print("This is python fatal test stdout")
    bb.fatal("This is a fatal error")
}
addtask do_pythontest_fatal
