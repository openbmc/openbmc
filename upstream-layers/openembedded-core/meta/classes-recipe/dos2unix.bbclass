#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

# Class for use to convert all CRLF line terminators to LF
# provided that some projects are being developed/maintained
# on Windows so they have different line terminators(CRLF) vs
# on Linux(LF), which can cause annoying patching errors during
# git push/checkout processes.

do_convert_crlf_to_lf[depends] += "dos2unix-native:do_populate_sysroot"

# Convert CRLF line terminators to LF
do_convert_crlf_to_lf () {
	find ${S} -type f -exec dos2unix {} \;
}

addtask convert_crlf_to_lf after do_unpack before do_patch
