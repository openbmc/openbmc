#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

do_install[postfuncs] += "install_relative_symlinks"

python install_relative_symlinks () {
    oe.path.replace_absolute_symlinks(d.getVar('D'), d)
}
