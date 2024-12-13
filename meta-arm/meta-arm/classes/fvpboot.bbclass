# Image class to write .fvpconf files for use with runfvp. If this is desired
# then add fvpboot to IMAGE_CLASSES, and set the variables below in your machine
# configuration as appropriate.

# Name of recipe providing FVP executable. If unset then the executable must be installed on the host.
FVP_PROVIDER ?= ""
# Name of FVP executable to run
FVP_EXE ?= ""
# Flags for --parameter/-C
FVP_CONFIG ?= ""
# Flags for --data
FVP_DATA ?= ""
# Flags for --application
FVP_APPLICATIONS ?= ""
# Flags to name serial terminals. Flag name is the terminal id (such as
# terminal_0), value is a human-readable name. If the name is not set
# then runfvp will hide the terminal.
FVP_TERMINALS ?= ""
# What terminal should be considered the primary console
FVP_CONSOLE ?= ""
# Flags for console names, as they appear in the FVP output. Flag name is an
# application-specific id for the console for use in test cases
FVP_CONSOLES[default] ?= "${FVP_CONSOLE}"
# Arbitrary extra arguments
FVP_EXTRA_ARGS ?= ""
# Bitbake variables to pass to the FVP environment
FVP_ENV_PASSTHROUGH ?= "ARMLMD_LICENSE_FILE"
FVP_ENV_PASSTHROUGH[vardeps] = "${FVP_ENV_PASSTHROUGH}"

EXTRA_IMAGEDEPENDS += "${FVP_PROVIDER}"

IMAGE_CLASSES += "image-artifact-names"

IMAGE_POSTPROCESS_COMMAND += "do_write_fvpboot_conf;"
python do_write_fvpboot_conf() {
    # Note that currently this JSON file is in development and the format may
    # change at any point, so it should always be used with a matching runfvp.

    import json, shlex

    if not d.getVar("FVP_EXE"):
        return

    conffile = os.path.join(d.getVar("IMGDEPLOYDIR"), d.getVar("IMAGE_NAME") + ".fvpconf")
    conffile_link = os.path.join(d.getVar("IMGDEPLOYDIR"), d.getVar("IMAGE_LINK_NAME") + ".fvpconf")

    data = {}
    provider = d.getVar("FVP_PROVIDER")
    if provider:
        data["provider"] = provider
        data["fvp-bindir"] = os.path.join(d.getVar("COMPONENTS_DIR"),
                                            d.getVar("BUILD_ARCH"),
                                            provider,
                                            "usr", "bin")

    def getFlags(varname):
        flags = d.getVarFlags(varname)
        # For unexplained reasons, getVarFlags() returns None if there are no flags
        if flags is None:
            return {}
        # For other reasons, you can't pass expand=True
        return {key: d.expand(value) for key, value in flags.items()}

    data["exe"] = d.getVar("FVP_EXE")
    data["parameters"] = getFlags("FVP_CONFIG")
    data["data"] = shlex.split(d.getVar("FVP_DATA") or "")
    data["applications"] = getFlags("FVP_APPLICATIONS")
    data["consoles"] = getFlags("FVP_CONSOLES")
    data["terminals"] = getFlags("FVP_TERMINALS")
    data["args"] = shlex.split(d.getVar("FVP_EXTRA_ARGS") or "")

    data["env"] = {}
    for var in d.getVar("FVP_ENV_PASSTHROUGH").split():
        if d.getVar(var) is not None:
            data["env"][var] = d.getVar(var)

    os.makedirs(os.path.dirname(conffile), exist_ok=True)
    with open(conffile, "wt") as f:
        json.dump(data, f)

    if conffile_link != conffile:
        if os.path.lexists(conffile_link):
           os.remove(conffile_link)
        os.symlink(os.path.basename(conffile), conffile_link)
}

def fvpboot_vars(d):
    vars = ['DEPLOY_DIR_IMAGE', 'IMAGE_NAME', 'IMAGE_LINK_NAME', 'COMPONENTS_DIR', 'BUILD_ARCH']
    vars.extend((k for k in d.keys() if k.startswith('FVP_')))
    return " ".join(vars)

do_write_fvpboot_conf[vardeps] += "${@fvpboot_vars(d)}"
