inherit ptest-gnome

TEST_FILES_PATH = "${datadir}/installed-tests/gstreamer-1.0/test-files"
RUN_PTEST_FILE = "${D}${PTEST_PATH}/run-ptest"

EXTRA_OEMESON += "-Dtest-files-path=${TEST_FILES_PATH}"

GST_TEST_SUITE_NAME ?= "gstreamer-1.0"

# Using do_install_ptest_base instead of do_install_ptest, since
# the default do_install_ptest_base is hardcoded to expect Makefiles.
do_install_ptest_base() {
    # Generate run-ptest file
    echo "#!/usr/bin/env sh" > "${RUN_PTEST_FILE}"
    echo "gnome-desktop-testing-runner ${GST_TEST_SUITE_NAME}" >> "${RUN_PTEST_FILE}"
    chmod 0755 "${RUN_PTEST_FILE}"

    # Install additional files required by tests
    if [ -d "${S}/tests/files" ] ; then
        install -d "${D}/${TEST_FILES_PATH}"
        install -m 0644 "${S}/tests/files"/* "${D}/${TEST_FILES_PATH}"
    fi
}
