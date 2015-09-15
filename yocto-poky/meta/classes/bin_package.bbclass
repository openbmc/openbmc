#
# ex:ts=4:sw=4:sts=4:et
# -*- tab-width: 4; c-basic-offset: 4; indent-tabs-mode: nil -*-
#
# Common variable and task for the binary package recipe.
# Basic principle:
# * The files have been unpacked to ${S} by base.bbclass
# * Skip do_configure and do_compile
# * Use do_install to install the files to ${D}
#
# Note:
# The "subdir" parameter in the SRC_URI is useful when the input package
# is rpm, ipk, deb and so on, for example:
#
# SRC_URI = "http://foo.com/foo-1.0-r1.i586.rpm;subdir=foo-1.0"
#
# Then the files would be unpacked to ${WORKDIR}/foo-1.0, otherwise
# they would be in ${WORKDIR}.
#

# Skip the unwanted steps
do_configure[noexec] = "1"
do_compile[noexec] = "1"

# Install the files to ${D}
bin_package_do_install () {
    # Do it carefully
    [ -d "${S}" ] || exit 1
    cd ${S} || exit 1
    tar --no-same-owner --exclude='./patches' --exclude='./.pc' -cpf - . \
        | tar --no-same-owner -xpf - -C ${D}
}

FILES_${PN} = "/"

EXPORT_FUNCTIONS do_install
