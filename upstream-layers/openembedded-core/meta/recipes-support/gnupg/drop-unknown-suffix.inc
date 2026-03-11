#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

#
# This .inc file is used to remove unknown suffix in runtime version
# for gnupg and its related packages.
#
# In these packages, if autogen.sh is run outside of a git repo,
# the find-version function always assumes that the package is a
# beta version and adds the suffix '-unknown' to the version number.
#
# This .inc file modifies autogen.sh to:
# 1. Replace beta=yes with beta=no
# 2. Replace tmp="-unknown" with tmp=""
#

do_configure:prepend() {
    if [ -f ${S}/autogen.sh ]; then
        sed -i \
            -e 's/^\([[:space:]]*\)beta=yes$/\1beta=no/' \
            -e 's/^\([[:space:]]*\)tmp="-unknown"$/\1tmp=""/' \
            ${S}/autogen.sh
    else
        bbwarn "autogen.sh not found in ${S}."
    fi
}
