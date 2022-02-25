#!/bin/bash
#
# SPDX-License-Identifier: GPL-2.0-only
#
# Perform an audit of which packages provide documentation and which
# are missing -doc packages.
#
# Setup requirements: be sure to be building for MACHINE=qemux86. Run
# this script after source'ing the build environment script, so you're
# running it from build/ directory.
#

REPORT_DOC_SIMPLE="documentation_exists.txt"
REPORT_DOC_DETAIL="documentation_exists_detail.txt"
REPORT_MISSING_SIMPLE="documentation_missing.txt"
REPORT_MISSING_DETAIL="documentation_missing_detail.txt"
REPORT_BUILD_ERRORS="build_errors.txt"

rm -rf $REPORT_DOC_SIMPLE $REPORT_DOC_DETAIL $REPORT_MISSING_SIMPLE $REPORT_MISSING_DETAIL

BITBAKE=`which bitbake`
if [ -z "$BITBAKE" ]; then
	echo "Error: bitbake command not found."
	echo "Did you forget to source the build environment script?"
	exit 1
fi

echo "REMINDER: you need to build for MACHINE=qemux86 or you won't get useful results"
echo "REMINDER: you need to set LICENSE_FLAGS_ACCEPTED appropriately in local.conf or "
echo " you'll get false positives.  For example, LICENSE_FLAGS_ACCEPTED = \"commercial\""

for pkg in `bitbake -s | awk '{ print \$1 }'`; do
	if [[ "$pkg" == "Loading" || "$pkg" == "Loaded" ||
	  "$pkg" == "Recipe"  ||
          "$pkg" == "Parsing" || "$pkg" == "Package" ||
          "$pkg" == "NOTE:"   || "$pkg" == "WARNING:" ||
          "$pkg" == "done."   || "$pkg" == "===========" ]]
	then
		# Skip initial bitbake output
		continue
	fi
	if [[ "$pkg" =~ -native$ || "$pkg" =~ -nativesdk$ ||
          "$pkg" =~ -cross-canadian ]]; then
		# Skip native/nativesdk/cross-canadian recipes
		continue
	fi
	if [[ "$pkg" =~ ^meta- || "$pkg" =~ ^packagegroup- || "$pkg" =~ -image ]]; then
		# Skip meta, task and image recipes
		continue
	fi
	if [[ "$pkg" =~ ^glibc- || "$pkg" =~ ^libiconv$ ||
          "$pkg" =~ -toolchain$ || "$pkg" =~ ^package-index$ ||
          "$pkg" =~ ^linux- || "$pkg" =~ ^adt-installer$ ||
          "$pkg" =~ ^eds-tools$ || "$pkg" =~ ^external-python-tarball$ ||
          "$pkg" =~ ^qt4-embedded$ || "$pkg" =~ ^qt-mobility ]]; then
		# Skip glibc, libiconv, -toolchain, and other recipes known
		# to cause build conflicts or trigger false positives.
		continue
	fi	

	echo "Building package $pkg..."
	bitbake $pkg > /dev/null
	if [ $? -ne 0 ]; then
		echo "There was an error building package $pkg" >> "$REPORT_MISSING_DETAIL"
		echo "$pkg" >> $REPORT_BUILD_ERRORS

		# Do not skip the remaining tests, as sometimes the
		# exit status is 1 due to QA errors, and we can still
		# perform the -doc checks.
	fi

	echo "$pkg built successfully, checking for a documentation package..."
	WORKDIR=`bitbake -e $pkg | grep ^WORKDIR | awk -F '=' '{ print \$2 }' | awk -F '"' '{ print \$2 }'`
	FIND_DOC_PKG=`find $WORKDIR/packages-split/*-doc -maxdepth 0 -type d`
	if [ -z "$FIND_DOC_PKG" ]; then
		# No -doc package was generated:
		echo "No -doc package: $pkg" >> "$REPORT_MISSING_DETAIL"
		echo "$pkg" >> $REPORT_MISSING_SIMPLE
		continue
	fi

	FIND_DOC_FILES=`find $FIND_DOC_PKG -type f`
	if [ -z "$FIND_DOC_FILES" ]; then
		# No files shipped with the -doc package:
		echo "No files shipped with the -doc package: $pkg" >> "$REPORT_MISSING_DETAIL"
		echo "$pkg" >> $REPORT_MISSING_SIMPLE
		continue
	fi

	echo "Documentation shipped with $pkg:" >> "$REPORT_DOC_DETAIL"
	echo "$FIND_DOC_FILES" >> "$REPORT_DOC_DETAIL"
	echo ""	>> "$REPORT_DOC_DETAIL"

	echo "$pkg" >> "$REPORT_DOC_SIMPLE"
done
