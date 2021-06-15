inherit terminal

python do_ccmake() {
    import shutil

    # copy current config for diffing
    config = os.path.join(d.getVar("B"), "CMakeCache.txt")
    if os.path.exists(config):
        shutil.copy(config, config + ".orig")

    oe_terminal(d.expand("ccmake ${OECMAKE_GENERATOR_ARGS} ${OECMAKE_SOURCEPATH} -Wno-dev"),
        d.getVar("PN") + " - ccmake", d)

    if os.path.exists(config) and os.path.exists(config + ".orig"):
        if bb.utils.md5_file(config) != bb.utils.md5_file(config + ".orig"):
            # the cmake class uses cmake --build, which will by default
            # regenerate configuration, simply mark the compile step as tainted
            # to ensure it is re-run
            bb.note("Configuration changed, recompile will be forced")
            bb.build.write_taint('do_compile', d)

}
do_ccmake[depends] += "cmake-native:do_populate_sysroot"
do_ccmake[nostamp] = "1"
do_ccmake[dirs] = "${B}"
addtask ccmake after do_configure

def cmake_parse_config_cache(path):
    with open(path, "r") as f:
        for i in f:
            i = i.rstrip("\n")
            if len(i) == 0 or i.startswith("//") or i.startswith("#"):
                continue # empty or comment
            key, value = i.split("=", 1)
            key, keytype = key.split(":")
            if keytype in ["INTERNAL", "STATIC"]:
                continue # skip internal and static config options
            yield key, keytype, value

def cmake_diff_config_vars(a, b):
    removed, added = [], []

    for ak, akt, av in a:
        found = False
        for bk, bkt, bv in b:
            if bk == ak:
                found = True
                if bkt != akt or bv != av: # changed
                    removed.append((ak, akt, av))
                    added.append((bk, bkt, bv))
                break
        # remove any missing from b
        if not found:
            removed.append((ak, akt, av))

    # add any missing from a
    for bk, bkt, bv in b:
        if not any(bk == ak for ak, akt, av in a):
            added.append((bk, bkt, bv))

    return removed, added

python do_ccmake_diffconfig() {
    import shutil
    config = os.path.join(d.getVar("B"), "CMakeCache.txt")
    if os.path.exists(config) and os.path.exists(config + ".orig"):
        if bb.utils.md5_file(config) != bb.utils.md5_file(config + ".orig"):
            # scan the changed options
            old = list(cmake_parse_config_cache(config + ".orig"))
            new = list(cmake_parse_config_cache(config))
            _, added = cmake_diff_config_vars(old, new)

            if len(added) != 0:
                with open(d.expand("${WORKDIR}/configuration.inc"), "w") as f:
                    f.write("EXTRA_OECMAKE += \" \\\n")
                    for k, kt, v in added:
                        escaped = v if " " not in v else "\"{0}\"".format(v)
                        f.write("    -D{0}:{1}={2} \\\n".format(k, kt, escaped))
                    f.write("    \"\n")
                bb.plain("Configuration recipe fragment written to: {0}".format(d.expand("${WORKDIR}/configuration.inc")))

                with open(d.expand("${WORKDIR}/site-file.cmake"), "w") as f:
                    for k, kt, v in added:
                        f.write("SET({0} \"{1}\" CACHE {2} \"\")\n".format(k, v, kt))
                bb.plain("Configuration cmake fragment written to: {0}".format(d.expand("${WORKDIR}/site-file.cmake")))

                # restore the original config
                shutil.copy(config + ".orig", config)
        else:
            bb.plain("No configuration differences, skipping configuration fragment generation.")
    else:
        bb.fatal("No config files found. Did you run ccmake?")
}
do_ccmake_diffconfig[nostamp] = "1"
do_ccmake_diffconfig[dirs] = "${B}"
addtask ccmake_diffconfig

