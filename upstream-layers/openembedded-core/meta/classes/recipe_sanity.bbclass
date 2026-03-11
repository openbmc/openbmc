#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

def __note(msg, d):
    bb.note("%s: recipe_sanity: %s" % (d.getVar("P"), msg))

__recipe_sanity_badruntimevars = "RDEPENDS RPROVIDES RRECOMMENDS RCONFLICTS"
def bad_runtime_vars(cfgdata, d):
    if bb.data.inherits_class("native", d) or \
            bb.data.inherits_class("cross", d):
        return

    for var in d.getVar("__recipe_sanity_badruntimevars").split():
        val = d.getVar(var, False)
        if val and val != cfgdata.get(var):
            __note("%s should be %s:${PN}" % (var, var), d)

__recipe_sanity_reqvars = "DESCRIPTION"
__recipe_sanity_reqdiffvars = ""
def req_vars(cfgdata, d):
    for var in d.getVar("__recipe_sanity_reqvars").split():
        if not d.getVar(var, False):
            __note("%s should be set" % var, d)

    for var in d.getVar("__recipe_sanity_reqdiffvars").split():
        val = d.getVar(var, False)
        cfgval = cfgdata.get(var)

        if not val:
            __note("%s should be set" % var, d)
        elif val == cfgval:
            __note("%s should be defined to something other than default (%s)" % (var, cfgval), d)

def var_renames_overwrite(cfgdata, d):
    renames = d.getVar("__recipe_sanity_renames", False)
    if renames:
        for (key, newkey, oldvalue, newvalue) in renames:
            if oldvalue != newvalue and oldvalue != cfgdata.get(newkey):
                __note("rename of variable '%s' to '%s' overwrote existing value '%s' with '%s'." % (key, newkey, oldvalue, newvalue), d)

def incorrect_nonempty_PACKAGES(cfgdata, d):
    if bb.data.inherits_class("native", d) or \
            bb.data.inherits_class("cross", d):
        if d.getVar("PACKAGES"):
            return True

def can_use_autotools_base(cfgdata, d):
    cfg = d.getVar("do_configure")
    if not bb.data.inherits_class("autotools", d):
        return False

    for i in ["autoreconf"] + ["%s_do_configure" % cls for cls in ["gnomebase", "gnome", "e", "autotools", "efl", "gpephone", "openmoko", "openmoko2", "xfce", "xlibs"]]:
        if cfg.find(i) != -1:
            return False

    for clsfile in d.getVar("__inherit_cache", False):
        (base, _) = os.path.splitext(os.path.basename(clsfile))
        if cfg.find("%s_do_configure" % base) != -1:
            __note("autotools_base usage needs verification, spotted %s_do_configure" % base, d)

    return True

def can_delete_FILESPATH(cfgdata, d):
    expected = cfgdata.get("FILESPATH")
    expectedpaths = d.expand(expected)
    unexpanded = d.getVar("FILESPATH", False)
    filespath = d.getVar("FILESPATH").split(":")
    filespath = [os.path.normpath(f) for f in filespath if os.path.exists(f)]
    for fp in filespath:
        if not fp in expectedpaths:
            # __note("Path %s in FILESPATH not in the expected paths %s" %
            # (fp, expectedpaths), d)
            return False
    return expected != unexpanded

def can_delete_others(p, cfgdata, d):
    for k in ["S", "PV", "PN", "DESCRIPTION", "DEPENDS",
              "SECTION", "PACKAGES", "EXTRA_OECONF", "EXTRA_OEMAKE"]:
    #for k in cfgdata:
        unexpanded = d.getVar(k, False)
        cfgunexpanded = cfgdata.get(k)
        if not cfgunexpanded:
            continue

        try:
            expanded = d.getVar(k)
            cfgexpanded = d.expand(cfgunexpanded)
        except bb.fetch.ParameterError:
            continue

        if unexpanded != cfgunexpanded and \
           cfgexpanded == expanded:
           __note("candidate for removal of %s" % k, d)
           bb.debug(1, "%s: recipe_sanity:   cfg's '%s' and d's '%s' both expand to %s" %
                       (p, cfgunexpanded, unexpanded, expanded))

python do_recipe_sanity () {
    p = d.getVar("P")
    p = "%s %s %s" % (d.getVar("PN"), d.getVar("PV"), d.getVar("PR"))

    sanitychecks = [
        (can_delete_FILESPATH, "candidate for removal of FILESPATH"),
        #(can_use_autotools_base, "candidate for use of autotools_base"),
        (incorrect_nonempty_PACKAGES, "native or cross recipe with non-empty PACKAGES"),
    ]
    cfgdata = d.getVar("__recipe_sanity_cfgdata", False)

    for (func, msg) in sanitychecks:
        if func(cfgdata, d):
            __note(msg, d)

    can_delete_others(p, cfgdata, d)
    var_renames_overwrite(cfgdata, d)
    req_vars(cfgdata, d)
    bad_runtime_vars(cfgdata, d)
}
do_recipe_sanity[nostamp] = "1"
addtask recipe_sanity

do_recipe_sanity_all[nostamp] = "1"
do_recipe_sanity_all[recrdeptask] = "do_recipe_sanity_all do_recipe_sanity"
do_recipe_sanity_all () {
    :
}
addtask recipe_sanity_all after do_recipe_sanity

python recipe_sanity_eh () {
    d = e.data

    cfgdata = {}
    for k in d.keys():
        if not isinstance(d.getVar(k, False), bb.data_smart.DataSmart):
            cfgdata[k] = d.getVar(k, False)

    d.setVar("__recipe_sanity_cfgdata", cfgdata)
    #d.setVar("__recipe_sanity_cfgdata", d)

    # Sick, very sick..
    from bb.data_smart import DataSmart
    old = DataSmart.renameVar
    def myrename(self, key, newkey):
        oldvalue = self.getVar(newkey, 0)
        old(self, key, newkey)
        newvalue = self.getVar(newkey, 0)
        if oldvalue:
            renames = self.getVar("__recipe_sanity_renames", 0) or set()
            renames.add((key, newkey, oldvalue, newvalue))
            self.setVar("__recipe_sanity_renames", renames)
    DataSmart.renameVar = myrename
}
addhandler recipe_sanity_eh
recipe_sanity_eh[eventmask] = "bb.event.ConfigParsed"
