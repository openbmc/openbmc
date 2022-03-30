DESCRIPTION = "Utilities for testing Power Management"
HOMEPAGE = "https://wiki.linaro.org/WorkingGroups/PowerManagement/Resources/TestSuite/PmQa"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

PV = "0.5.2"

BRANCH ?= "master"

SRCREV = "05710ec5032be4c8edafb4109d4d908d31243906"

SRC_URI = "git://git.linaro.org/power/pm-qa.git;protocol=git;branch=${BRANCH}"

S = "${WORKDIR}/git"

CFLAGS += "-pthread"

do_compile () {
    # Find all the .c files in this project and build them.
    for x in `find . -name "*.c"`
    do
        util=`echo ${x} | sed s/.c$//`
        oe_runmake ${util}
    done
}

do_install () {
    install -d ${D}${bindir}
    install -d ${D}${libdir}/${BPN}

    # Install the compiled binaries that were built in the previous step
    for x in `find . -name "*.c"`
    do
        util=`echo ${x} | sed s/.c$//`
        util_basename=`basename ${util}`
        install -m 0755 ${util} ${D}${bindir}/${util_basename}
    done

    # Install the helper scripts in a subdirectory of $libdir
    for script in `find . -name "*.sh" | grep include`
    do
        # Remove hardcoded relative paths
        sed -i -e 's#..\/utils\/##' ${script}
        sed -i -e 's#. ..\/Switches#${bindir}#g' ${script}

        script_basename=`basename ${script}`
        install -m 0755 $script ${D}${libdir}/${BPN}/${script_basename}
    done

    # Install the shell scripts NOT in the $libdir directory since those
    # will be installed elsewhere
    for script in `find . -name "*.sh" | grep -v include`
    do
        # if the script includes any helper scripts from the $libdir
        # directory then change the source path to the absolute path
        # to reflect the install location of the helper scripts.
        sed -i -e "s#. ../include#. ${libdir}/${BPN}#g" ${script}
        # Remove hardcoded relative paths
        sed -i -e 's#..\/utils\/##' ${script}

        script_basename=`basename ${script}`
        install -m 0755 $script ${D}${bindir}/${script_basename}
    done
}
RDEPENDS:${PN} +="bash"

# http://errors.yoctoproject.org/Errors/Details/186956/
COMPATIBLE_HOST:libc-musl = 'null'
