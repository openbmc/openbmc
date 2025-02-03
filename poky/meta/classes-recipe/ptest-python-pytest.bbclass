#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

inherit ptest

# Overridable configuration for the directory within the source tree
# containing the pytest files
PTEST_PYTEST_DIR ?= "tests"

do_install_ptest() {
	# Check if the recipe provides its own version of run-ptest
	# If nothing exists in the SRC_URI, dynamically create a 
	# run-test script of "last resort" that has the default
	# pytest behavior.
	# 
	# Users can override this behavior by simply including a
	# custom script (run-ptest) in the source file list
	if [ ! -f "${UNPACKDIR}/run-ptest" ]; then
		cat > ${D}${PTEST_PATH}/run-ptest << EOF
#!/bin/sh
pytest --automake
EOF
		# Ensure the newly created script has the execute bit set
		chmod 755 ${D}${PTEST_PATH}/run-ptest
	fi
	if [ -d "${S}/${PTEST_PYTEST_DIR}" ]; then
		install -d ${D}${PTEST_PATH}/${PTEST_PYTEST_DIR}
		cp -rf ${S}/${PTEST_PYTEST_DIR}/* ${D}${PTEST_PATH}/${PTEST_PYTEST_DIR}/
	fi
}

FILES:${PN}-ptest:prepend = "${PTEST_PATH}/*"

RDEPENDS:${PN}-ptest:prepend = "python3-pytest python3-unittest-automake-output "
