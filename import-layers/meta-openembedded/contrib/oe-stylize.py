#!/usr/bin/env python

"""\
Sanitize a bitbake file following the OpenEmbedded style guidelines,
see http://openembedded.org/wiki/StyleGuide 

(C) 2006 Cyril Romain <cyril.romain@gmail.com>
MIT license

TODO: 
 - add the others OpenEmbedded variables commonly used:
 - parse command arguments and print usage on misuse
    . prevent giving more than one .bb file in arguments
 - write result to a file
 - backup the original .bb file
 - make a diff and ask confirmation for patching ?
 - do not use startswith only:
    /!\ startswith('SOMETHING') is not taken into account due to the previous startswith('S').
 - count rule breaks and displays them in the order frequence
"""

from __future__ import print_function 
import fileinput
import string
import re

__author__ = "Cyril Romain <cyril.romain@gmail.com>"
__version__ = "$Revision: 0.5 $"

# The standard set of variables often found in .bb files in the preferred order
OE_vars = [
    'SUMMARY',
    'DESCRIPTION',
    'AUTHOR',
    'HOMEPAGE',
    'SECTION',
    'LICENSE',
    'LIC_FILES_CHKSUM',
    'DEPENDS',
    'PROVIDES',
    'SRCREV',
    'SRCDATE',
    'PE',
    'PV',
    'PR',
    'INC_PR',
    'SRC_URI',
    'S',
    'GPE_TARBALL_SUFFIX',
    'inherit',
    'EXTRA_',
    'export',
    'do_fetch',
    'do_unpack',
    'do_patch',
    'WORKDIR',
    'acpaths',
    'do_configure',
    'do_compile',
    'do_install',
    'PACKAGES',
    'PACKAGE_ARCH',
    'RDEPENDS',
    'RRECOMMENDS',
    'RSUGGESTS',
    'RPROVIDES',
    'RCONFLICTS',
    'FILES',    
    'do_package',
    'do_stage',
    'addhandler',
    'addtask',
    'bindir',
    'headers',
    'include',
    'includedir',
    'python',
    'qtopiadir',
    'pkg_preins',
    'pkg_prerm',
    'pkg_postins',
    'pkg_postrm',
    'require',
    'sbindir',
    'basesysconfdir',
    'sysconfdir',
    'ALLOW_EMPTY',
    'ALTERNATIVE_NAME',
    'ALTERNATIVE_PATH',
    'ALTERNATIVE_LINK',
    'ALTERNATIVE_PRIORITY',
    'ALTNAME',
    'AMD_DRIVER_LABEL',
    'AMD_DRIVER_VERSION',
    'ANGSTROM_EXTRA_INSTALL',
    'APPDESKTOP',
    'APPIMAGE',
    'APPNAME',
    'APPTYPE',
    'APPWEB_BUILD',
    'APPWEB_HOST',
    'AR',
    'ARCH',
    'ARM_INSTRUCTION_SET',
    'ARM_MUTEX',
    'ART_CONFIG',
    'B',
    'BJAM_OPTS',
    'BJAM_TOOLS',
    'BONOBO_HEADERS',
    'BOOTSCRIPTS',
    'BROKEN',
    'BUILD_CPPFLAGS',
    'CFLAGS',
    'CCFLAGS',
    'CMDLINE',
    'COLLIE_MEMORY_SIZE',
    'COMPATIBLE_HOST',
    'COMPATIBLE_MACHINE',
    'COMPILE_HERMES',
    'CONFFILES',
    'CONFLICTS',
    'CORE_EXTRA_D',
    'CORE_IMAGE_EXTRA_INSTALL',
    'CORE_PACKAGES_D',
    'CORE_PACKAGES_RD',
    'CPPFLAGS',
    'CVSDATE',
    'CXXFLAGS',
    'DEBIAN_NOAUTONAME',
    'DEBUG_APPS',
    'DEFAULT_PREFERENCE',
    'DB4_CONFIG',
    'EXCLUDE_FROM_SHLIBS',
    'EXCLUDE_FROM_WORLD',
    'FIXEDSRCDATE',
    'GLIBC_ADDONS',
    'GLIBC_EXTRA_OECONF',
    'GNOME_VFS_HEADERS',
    'HEADERS',
    'INHIBIT_DEFAULT_DEPS',
    'INITSCRIPT_PACKAGES',
    'INITSCRIPT_NAME',
    'INITSCRIPT_PARAMS',
    'INSANE_SKIP',
    'PACKAGE_INSTALL',
    'KERNEL_IMAGETYPE',
    'KERNEL_IMAGEDEST',
    'KERNEL_OUTPUT',
    'KERNEL_RELEASE',
    'KERNEL_PRIORITY',
    'KERNEL_SOURCE',
    'KERNEL_SUFFIX',
    'KERNEL_VERSION',
    'K_MAJOR',
    'K_MICRO',
    'K_MINOR',
    'HHV',
    'KV',
    'LDFLAGS',
    'LD',
    'LD_SO',
    'LDLIBS',
    'LEAD_SONAME',
    'LIBTOOL',
    'LIBBDB_EXTRA',
    'LIBV',
    'MACHINE_ESSENTIAL_EXTRA_RDEPENDS',
    'MACHINE_ESSENTIAL_EXTRA_RRECOMMENDS',
    'MACHINE_EXTRA_RDEPENDS',
    'MACHINE_EXTRA_RRECOMMENDS',
    'MACHINE_FEATURES',
    'MACHINE_TASKS',
    'MACHINE',
    'MACHTYPE',
    'MAKE_TARGETS',
    'MESSAGEUSER',
    'MESSAGEHOME',
    'MIRRORS',
    'MUTEX',
    'OE_QMAKE_INCDIR_QT',
    'OE_QMAKE_CXXFLAGS',
    'ORBIT_IDL_SRC',
    'PARALLEL_MAKE',
    'PAKCAGE_ARCH',
    'PCMCIA_MANAGER',
    'PKG_BASENAME',
    'PKG',
    'QEMU',
    'QMAKE_PROFILES',
    'QPEDIR',
    'QPF_DESCRIPTION',
    'QPF_PKGPATTERN',
    'QT_CONFIG_FLAGS',
    'QT_LIBRARY',
    'ROOTFS_POSTPROCESS_COMMAND',
    'RREPLACES',
    'TARGET_CFLAGS',
    'TARGET_CPPFLAGS',
    'TARGET_LDFLAGS',
    'UBOOT_MACHINE',
    'UCLIBC_BASE',
    'UCLIBC_PATCHES',
    'USERADD_PACKAGES',
    'USERADD_PARAM',
    'VIRTUAL_NAME',
    'XORG_PN',
    'XSERVER',
    'others'
]

varRegexp = r'^([a-zA-Z_0-9${}-]*)([ \t]*)([+.:]?=[+.]?)([ \t]*)([^\t]+)'
routineRegexp = r'^([a-zA-Z0-9_ ${}-]+?)\('

# Variables seen in the processed .bb
seen_vars = {}
for v in OE_vars: 
    seen_vars[v] = []

# _Format guideline #0_: 
#   No spaces are allowed at the beginning of lines that define a variable or 
#   a do_ routine
def respect_rule0(line): 
    return line.lstrip()==line
def conformTo_rule0(line): 
    return line.lstrip()

# _Format guideline #1_: 
#   No spaces are allowed behind the line continuation symbol '\'
def respect_rule1(line):
    if line.rstrip().endswith('\\'):
        return line.endswith('\\')
    else: 
        return True
def conformTo_rule1(line):
    return line.rstrip()

# _Format guideline #2_: 
#   Tabs should not be used (use spaces instead).
def respect_rule2(line):
    return line.count('\t')==0
def conformTo_rule2(line):
    return line.expandtabs()

# _Format guideline #3_:
#   Comments inside bb files are allowed using the '#' character at the 
#   beginning of a line.
def respect_rule3(line):
    if line.lstrip().startswith('#'):
        return line.startswith('#')
    else: 
        return True
def conformTo_rule3(line):
    return line.lstrip()

# _Format guideline #4_:
#   Use quotes on the right hand side of assignments FOO = "BAR"
def respect_rule4(line):
    r = re.search(varRegexp, line)
    if r is not None:
        r2 = re.search(r'("?)([^"\\]*)(["\\]?)', r.group(5))
        # do not test for None it because always match
        return r2.group(1)=='"' and r2.group(3)!=''
    return False
def conformTo_rule4(line):
    r = re.search(varRegexp, line)
    return ''.join([r.group(1), ' ', r.group(3), ' "', r.group(5), r.group(5).endswith('"') and '' or '"'])

# _Format guideline #5_:
#   The correct spacing for a variable is FOO = "BAR".
def respect_rule5(line):
    r = re.search(varRegexp, line)
    return r is not None and r.group(2)==" " and r.group(4)==" "
def conformTo_rule5(line):
    r = re.search(varRegexp, line)
    return ''.join([r.group(1), ' ', r.group(3), ' ', r.group(5)])

# _Format guideline #6_:
#   Don't use spaces or tabs on empty lines
def respect_rule6(line):
    return not line.isspace() or line=="\n"
def conformTo_rule6(line):
    return ""

# _Format guideline #7_:
#   Indentation of multiline variables such as SRC_URI is desireable.
def respect_rule7(line):
    return True
def conformTo_rule7(line):
    return line

rules = (
    (respect_rule0, conformTo_rule0, "No spaces are allowed at the beginning of lines that define a variable or a do_ routine"),
    (respect_rule1, conformTo_rule1, "No spaces are allowed behind the line continuation symbol '\\'"),
    (respect_rule2, conformTo_rule2, "Tabs should not be used (use spaces instead)"),
    (respect_rule3, conformTo_rule3, "Comments inside bb files are allowed using the '#' character at the beginning of a line"),
    (respect_rule4, conformTo_rule4, "Use quotes on the right hand side of assignments FOO = \"BAR\""),
    (respect_rule5, conformTo_rule5, "The correct spacing for a variable is FOO = \"BAR\""),
    (respect_rule6, conformTo_rule6, "Don't use spaces or tabs on empty lines"),
    (respect_rule7, conformTo_rule7, "Indentation of multiline variables such as SRC_URI is desireable"),
)

# Function to check that a line respects a rule. If not, it tries to conform
# the line to the rule. Reminder or Disgression message are dump accordingly.
def follow_rule(i, line):
    oldline = line
    # if the line does not respect the rule
    if not rules[i][0](line):
        # try to conform it to the rule
        line = rules[i][1](line)
        # if the line still does not respect the rule
        if not rules[i][0](line):
            # this is a rule disgression
            print ("## Disgression: ", rules[i][2], " in: '", oldline, "'")
        else:
            # just remind user about his/her errors
            print ("## Reminder: ", rules[i][2], " in : '", oldline, "'")
    return line


if __name__ == "__main__":

    # -- retrieves the lines of the .bb file --
    lines = []
    for line in fileinput.input():
        # use 'if True' to warn user about all the rule he/she breaks
        # use 'if False' to conform to rules{2,1,6} without warnings
        if True:
            lines.append(line)
        else:
            # expandtabs on each line so that rule2 is always respected 
            # rstrip each line so that rule1 is always respected 
            line = line.expandtabs().rstrip()
            # ignore empty lines (or line filled with spaces or tabs only)
            # so that rule6 is always respected
            if line is not '':
                lines.append(line)

    # -- parse the file --
    var = ""
    in_routine = False
    commentBloc = []
    olines = []
    for line in lines: 
        originalLine = line
        # rstrip line to remove line breaks characters
        line = line.rstrip()
        line = follow_rule(2, line)
        line = follow_rule(1, line)
        line = follow_rule(6, line)

        # ignore empty lines
        if line.isspace() or line is '':
            # flush comments into the olines
            for c in commentBloc: olines.append(c)
            commentBloc = []
            continue

        if line.startswith('}'): 
            in_routine=False
        keep = line.endswith('\\') or in_routine

        # handles commented lines
        if line.lstrip().startswith('#'):
            # check and follow rule3 if not in a variables or routines
            if not in_routine:
                line = follow_rule(3, line)
            commentBloc.append(line)
            continue

        if var in seen_vars:
            for c in commentBloc: seen_vars[var].append(c)
            commentBloc = []
            seen_vars[var].append(line)
        else:
            for k in OE_vars:
                if line.startswith(k):
                    var = k
                    break
            if re.match(routineRegexp, line) is not None: 
                in_routine=True
                line = follow_rule(0, line)
            elif re.match(varRegexp, line) is not None:
                line = follow_rule(0, line)
                line = follow_rule(4, line)
                line = follow_rule(5, line)
            if var == "":
                if not in_routine:
                    print ("## Warning: unknown variable/routine \"%s\"" % originalLine.rstrip('\n'))
                var = 'others'
            for c in commentBloc: seen_vars[var].append(c)
            commentBloc = []
            seen_vars[var].append(line)
        if not keep and not in_routine: var = ""

    # -- dump the sanitized .bb file --
    addEmptyLine = False
    # write comments that are not related to variables nor routines
    for l in commentBloc: olines.append(l)
    # write variables and routines
    previourVarPrefix = "unknown"
    for k in OE_vars:
        if k=='SRC_URI': addEmptyLine = True
        if seen_vars[k] != []: 
            if addEmptyLine and not k.startswith(previourVarPrefix):
                olines.append("")
            for l in seen_vars[k]: 
                olines.append(l)
            previourVarPrefix = k.split('_')[0]=='' and "unknown" or k.split('_')[0]
    for line in olines: print(line)

