#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

#
# Sanity check the users setup for common misconfigurations
#

SANITY_REQUIRED_UTILITIES ?= "patch diffstat git bzip2 tar \
    gzip gawk chrpath wget cpio perl file which"

# Functions added to this variable MUST throw a NotImplementedError exception unless
# they successfully changed the config version in the config file. Exceptions
# are used since exec_func doesn't handle return values.
BBLAYERS_CONF_UPDATE_FUNCS += " \
    conf/bblayers.conf:LCONF_VERSION:LAYER_CONF_VERSION:oecore_update_bblayers \
    conf/local.conf:CONF_VERSION:LOCALCONF_VERSION:oecore_update_localconf \
    conf/site.conf:SCONF_VERSION:SITE_CONF_VERSION:oecore_update_siteconf \
"

SANITY_DIFF_TOOL ?= "diff -u"

SANITY_LOCALCONF_SAMPLE ?= "${COREBASE}/meta*/conf/templates/default/local.conf.sample"
python oecore_update_localconf() {
    # Check we are using a valid local.conf
    current_conf  = d.getVar('CONF_VERSION')
    conf_version =  d.getVar('LOCALCONF_VERSION')

    failmsg = """Your version of local.conf was generated from an older/newer version of
local.conf.sample and there have been updates made to this file. Please compare the two
files and merge any changes before continuing.

Matching the version numbers will remove this message.

\"${SANITY_DIFF_TOOL} conf/local.conf ${SANITY_LOCALCONF_SAMPLE}\"

is a good way to visualise the changes."""
    failmsg = d.expand(failmsg)

    raise NotImplementedError(failmsg)
}

SANITY_SITECONF_SAMPLE ?= "${COREBASE}/meta*/conf/templates/default/site.conf.sample"
python oecore_update_siteconf() {
    # If we have a site.conf, check it's valid
    current_sconf = d.getVar('SCONF_VERSION')
    sconf_version = d.getVar('SITE_CONF_VERSION')

    failmsg = """Your version of site.conf was generated from an older version of
site.conf.sample and there have been updates made to this file. Please compare the two
files and merge any changes before continuing.

Matching the version numbers will remove this message.

\"${SANITY_DIFF_TOOL} conf/site.conf ${SANITY_SITECONF_SAMPLE}\"

is a good way to visualise the changes."""
    failmsg = d.expand(failmsg)

    raise NotImplementedError(failmsg)
}

SANITY_BBLAYERCONF_SAMPLE ?= "${COREBASE}/meta*/conf/templates/default/bblayers.conf.sample"
python oecore_update_bblayers() {
    # bblayers.conf is out of date, so see if we can resolve that

    current_lconf = int(d.getVar('LCONF_VERSION'))
    lconf_version = int(d.getVar('LAYER_CONF_VERSION'))

    failmsg = """Your version of bblayers.conf has the wrong LCONF_VERSION (has ${LCONF_VERSION}, expecting ${LAYER_CONF_VERSION}).
Please compare your file against bblayers.conf.sample and merge any changes before continuing.
"${SANITY_DIFF_TOOL} conf/bblayers.conf ${SANITY_BBLAYERCONF_SAMPLE}"

is a good way to visualise the changes."""
    failmsg = d.expand(failmsg)

    if not current_lconf:
        raise NotImplementedError(failmsg)

    lines = []

    if current_lconf < 4:
        raise NotImplementedError(failmsg)

    bblayers_fn = oe.sanity.bblayers_conf_file(d)
    lines = oe.sanity.sanity_conf_read(bblayers_fn)

    if current_lconf == 4 and lconf_version > 4:
        topdir_var = '$' + '{TOPDIR}'
        index, bbpath_line = oe.sanity.sanity_conf_find_line('BBPATH', lines)
        if bbpath_line:
            start = bbpath_line.find('"')
            if start != -1 and (len(bbpath_line) != (start + 1)):
                if bbpath_line[start + 1] == '"':
                    lines[index] = (bbpath_line[:start + 1] +
                                    topdir_var + bbpath_line[start + 1:])
                else:
                    if not topdir_var in bbpath_line:
                        lines[index] = (bbpath_line[:start + 1] +
                                    topdir_var + ':' + bbpath_line[start + 1:])
            else:
                raise NotImplementedError(failmsg)
        else:
            index, bbfiles_line = oe.sanity.sanity_conf_find_line('BBFILES', lines)
            if bbfiles_line:
                lines.insert(index, 'BBPATH = "' + topdir_var + '"\n')
            else:
                raise NotImplementedError(failmsg)

        current_lconf += 1
        oe.sanity.sanity_conf_update(bblayers_fn, lines, 'LCONF_VERSION', current_lconf)
        bb.note("Your conf/bblayers.conf has been automatically updated.")
        return

    elif current_lconf == 5 and lconf_version > 5:
        # Null update, to avoid issues with people switching between poky and other distros
        current_lconf = 6
        oe.sanity.sanity_conf_update(bblayers_fn, lines, 'LCONF_VERSION', current_lconf)
        bb.note("Your conf/bblayers.conf has been automatically updated.")
        return

    elif current_lconf == 6 and lconf_version > 6:
        # Handle rename of meta-yocto -> meta-poky
        # This marks the start of separate version numbers but code is needed in OE-Core
        # for the migration, one last time.
        layers = d.getVar('BBLAYERS').split()
        layers = [ os.path.basename(path) for path in layers ]
        if 'meta-yocto' in layers:
            found = False
            while True:
                index, meta_yocto_line = oe.sanity.sanity_conf_find_line(r'.*meta-yocto[\'"\s\n]', lines)
                if meta_yocto_line:
                    lines[index] = meta_yocto_line.replace('meta-yocto', 'meta-poky')
                    found = True
                else:
                    break
            if not found:
                raise NotImplementedError(failmsg)
            index, meta_yocto_line = oe.sanity.sanity_conf_find_line('LCONF_VERSION.*\n', lines)
            if meta_yocto_line:
                lines[index] = 'POKY_BBLAYERS_CONF_VERSION = "1"\n'
            else:
                raise NotImplementedError(failmsg)
            with open(bblayers_fn, "w") as f:
                f.write(''.join(lines))
            bb.note("Your conf/bblayers.conf has been automatically updated.")
            return
        current_lconf += 1
        oe.sanity.sanity_conf_update(bblayers_fn, lines, 'LCONF_VERSION', current_lconf)
        bb.note("Your conf/bblayers.conf has been automatically updated.")
        return

    raise NotImplementedError(failmsg)
}

addhandler config_reparse_eventhandler
config_reparse_eventhandler[eventmask] = "bb.event.ConfigParsed"
python config_reparse_eventhandler() {
    oe.sanity.sanity_check_conffiles(e.data)
}

addhandler check_sanity_eventhandler
check_sanity_eventhandler[eventmask] = "bb.event.SanityCheck bb.event.NetworkTest"
python check_sanity_eventhandler() {
    if bb.event.getName(e) == "SanityCheck":
        sanity_data = bb.data.createCopy(e.data)
        oe.sanity.check_sanity(sanity_data)
        if e.generateevents:
            sanity_data.setVar("SANITY_USE_EVENTS", "1")
        bb.event.fire(bb.event.SanityCheckPassed(), e.data)
    elif bb.event.getName(e) == "NetworkTest":
        sanity_data = bb.data.createCopy(e.data)
        if e.generateevents:
            sanity_data.setVar("SANITY_USE_EVENTS", "1")
        bb.event.fire(bb.event.NetworkTestFailed() if oe.sanity.check_connectivity(sanity_data) else bb.event.NetworkTestPassed(), e.data)

    return
}
