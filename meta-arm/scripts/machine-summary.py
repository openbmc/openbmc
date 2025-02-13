#! /usr/bin/env python3

import argparse
import datetime
import os
import pathlib
import re
import sys

import jinja2

def trim_pv(pv):
    """
    Strip anything after +git from the PV
    """
    return "".join(pv.partition("+git")[:2])

def needs_update(version, upstream):
    """
    Do a dumb comparison to determine if the version needs to be updated.
    """
    if "+git" in version:
        # strip +git and see if this is a post-release snapshot
        version = version.replace("+git", "")
    return version != upstream

def safe_patches(patches):
    for info in patches:
        if info["status"] in ("Denied", "Pending", "Unknown"):
            return False
    return True

def layer_path(layername: str, d) -> pathlib.Path:
    """
    Return the path to the specified layer, or None if the layer isn't present.
    """
    if not hasattr(layer_path, "cache"):
        # Don't use functools.lru_cache as we don't want d changing to invalidate the cache
        layer_path.cache = {}

    if layername in layer_path.cache:
        return layer_path.cache[layername]

    bbpath = d.getVar("BBPATH").split(":")
    pattern = d.getVar('BBFILE_PATTERN_' + layername)
    for path in reversed(sorted(bbpath)):
        if re.match(pattern, path + "/"):
            layer_path.cache[layername] = pathlib.Path(path)
            return layer_path.cache[layername]
    return None

def get_url_for_patch(layer: str, localpath: pathlib.Path, d) -> str:
    relative = localpath.relative_to(layer_path(layer, d))

    # TODO: use layerindexlib
    # TODO: assumes default branch
    if layer == "core":
        return f"https://git.openembedded.org/openembedded-core/tree/meta/{relative}"
    elif layer in ("meta-arm", "meta-arm-bsp", "arm-toolchain"):
        return f"https://git.yoctoproject.org/meta-arm/tree/{layer}/{relative}"
    else:
        print(f"WARNING: Don't know web URL for layer {layer}", file=sys.stderr)
        return None

def extract_patch_info(src_uri, d):
    """
    Parse the specified patch entry from a SRC_URI and return (base name, layer name, status) tuple
    """
    import bb.fetch, bb.utils

    info = {}
    localpath = pathlib.Path(bb.fetch.decodeurl(src_uri)[2])
    info["name"] = localpath.name
    info["layer"] = bb.utils.get_file_layer(str(localpath), d)
    info["url"] = get_url_for_patch(info["layer"], localpath, d)

    status = "Unknown"
    with open(localpath, errors="ignore") as f:
        m = re.search(r"^[\t ]*Upstream[-_ ]Status:?[\t ]*(\w*)", f.read(), re.IGNORECASE | re.MULTILINE)
        if m:
            # TODO: validate
            status = m.group(1)
    info["status"] = status
    return info

def harvest_data(machines, recipes):
    import bb.tinfoil
    with bb.tinfoil.Tinfoil() as tinfoil:
        tinfoil.prepare(config_only=True)
        corepath = layer_path("core", tinfoil.config_data)
        sys.path.append(os.path.join(corepath, "lib"))
    import oe.recipeutils
    import oe.patch

    # Queue of recipes that we're still looking for upstream releases for
    to_check = list(recipes)

    # Upstream releases
    upstreams = {}
    # Machines to recipes to versions
    versions = {}

    for machine in machines:
        print(f"Gathering data for {machine}...")
        os.environ["MACHINE"] = machine
        with bb.tinfoil.Tinfoil() as tinfoil:
            versions[machine] = {}

            tinfoil.prepare(quiet=2)
            for recipe in recipes:
                try:
                    d = tinfoil.parse_recipe(recipe)
                except bb.providers.NoProvider:
                    continue

                if recipe in to_check:
                    try:
                        info = oe.recipeutils.get_recipe_upstream_version(d)
                        upstreams[recipe] = info["version"]
                        to_check.remove(recipe)
                    except (bb.providers.NoProvider, KeyError):
                        pass

                details = versions[machine][recipe] = {}
                details["recipe"] = d.getVar("PN")
                details["version"] = trim_pv(d.getVar("PV"))
                details["fullversion"] = d.getVar("PV")
                details["patches"] = [extract_patch_info(p, d) for p in oe.patch.src_patches(d)]
                details["patched"] = bool(details["patches"])
                details["patches_safe"] = safe_patches(details["patches"])

    # Now backfill the upstream versions
    for machine in versions:
        for recipe in versions[machine]:
            data = versions[machine][recipe]
            data["upstream"] = upstreams[recipe]
            data["needs_update"] = needs_update(data["version"], data["upstream"])
    return upstreams, versions

# TODO can this be inferred from the list of recipes in the layer
recipes = ("boot-wrapper-aarch64",
           "edk2-firmware",
           "gator-daemon",
           "gn",
           "hafnium",
           "opencsd",
           "optee-ftpm",
           "optee-os",
           "sbsa-acs",
           "scp-firmware",
           "trusted-firmware-a",
           "trusted-firmware-m",
           "u-boot",
           "virtual/kernel")


class Format:
    """
    The name of this format
    """
    name = None
    """
    Registry of names to classes
    """
    registry = {}

    def __init_subclass__(cls, **kwargs):
        super().__init_subclass__(**kwargs)
        assert cls.name
        cls.registry[cls.name] = cls

    @classmethod
    def get_format(cls, name):
        return cls.registry[name]()

    def render(self, context, output: pathlib.Path):
        pass

    def get_template(self, name):
        template_dir = os.path.dirname(os.path.abspath(__file__))
        env = jinja2.Environment(
            loader=jinja2.FileSystemLoader(template_dir),
            extensions=['jinja2.ext.i18n'],
            autoescape=jinja2.select_autoescape(),
            trim_blocks=True,
            lstrip_blocks=True
        )

        # We only need i18n for plurals
        env.install_null_translations()

        return env.get_template(name)

class TextOverview(Format):
    name = "overview.txt"

    def render(self, context, output: pathlib.Path):
        with open(output, "wt") as f:
            f.write(self.get_template(f"machine-summary-overview.txt.jinja").render(context))

class HtmlUpdates(Format):
    name = "report"

    def render(self, context, output: pathlib.Path):
        if output.exists() and not output.is_dir():
            print(f"{output} is not a directory", file=sys.stderr)
            sys.exit(1)

        if not output.exists():
            output.mkdir(parents=True)

        with open(output / "index.html", "wt") as f:
            f.write(self.get_template(f"report-index.html.jinja").render(context))

        subcontext = context.copy()
        del subcontext["data"]
        for machine, subdata in context["data"].items():
            subcontext["machine"] = machine
            subcontext["data"] = subdata
            with open(output / f"{machine}.html", "wt") as f:
                f.write(self.get_template(f"report-details.html.jinja").render(subcontext))

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="machine-summary")
    parser.add_argument("machines", nargs="+", help="machine names", metavar="MACHINE")
    parser.add_argument("-t", "--type", required=True, choices=Format.registry.keys())
    parser.add_argument("-o", "--output", type=pathlib.Path, required=True)
    args = parser.parse_args()

    context = {}
    # TODO: include git describe for meta-arm
    context["timestamp"] = str(datetime.datetime.now().strftime("%c"))
    context["recipes"] = sorted(recipes)
    context["releases"], context["data"] = harvest_data(args.machines, recipes)

    formatter = Format.get_format(args.type)
    formatter.render(context, args.output)
