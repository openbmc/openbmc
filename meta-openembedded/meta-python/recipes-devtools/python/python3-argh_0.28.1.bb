SUMMARY = "An unobtrusive argparse wrapper with natural syntax"
DESCRIPTION = "Building a command-line interface? Found yourself uttering \
'argh!' while struggling with the API of argparse? Don't like the complexity \
but need the power? \
\
Everything should be made as simple as possible, but no simpler. \
\
—Albert Einstein (probably) \
\
Argh is a smart wrapper for argparse. Argparse is a very powerful \
tool; Argh just makes it easy to use."

LICENSE = "LGPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING.LESSER;md5=3000208d539ec061b899bce1d9ce9404 \
                    file://README.rst;beginline=261;endline=275;md5=a8bf3475df7e0d575a53d04c83d5e090"

SRC_URI[sha256sum] = "b2093086f0e809a3ecc24b64a2145309ee8f56d034936cd59e57c558a357329d"

inherit pypi python_flit_core

RDEPENDS:${PN} += " \
    python3-argcomplete \
    python3-logging \
"

BBCLASSEXTEND = "native nativesdk"
