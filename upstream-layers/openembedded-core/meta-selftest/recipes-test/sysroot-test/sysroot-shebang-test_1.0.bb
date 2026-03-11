SUMMARY = "Check that shebang does not exceed 128 characters"
LICENSE = "CLOSED"
INHIBIT_DEFAULT_DEPS = "1"

EXCLUDE_FROM_WORLD = "1"
do_install() {
    install -d ${D}${bindir}
    echo '#!BiM3cnVd1Amtv6PG+FynrQiVMbZnX5ELgF21q3EkuB+44JEGWtq8TvBJ7EGidfVs3eR3wVOUbLnjYDlKUWcm7YC/ute7f+KDHbwxziRUSUBZAUqgjiQdfQ0HnxajI0ozbM863E9JV9k13yZKYfh9/zR77Y6Dl4Dd3zOWS75LSpkAXV' > ${D}${bindir}/max-shebang
    chmod 755 ${D}${bindir}/max-shebang
}

BBCLASSEXTEND = "native"
