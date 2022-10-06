import json
import pathlib
import os


def get_image_directory(machine=None):
    """
    Get the DEPLOY_DIR_IMAGE for the specified machine
    (or the configured machine if not set).
    """
    try:
        import bb.tinfoil
    except ImportError as e:
        raise RuntimeError("Cannot connect to BitBake, did you oe-init-build-env?") from e

    if machine:
        os.environ["MACHINE"] = machine

    with bb.tinfoil.Tinfoil() as tinfoil:
        tinfoil.prepare(config_only=True)
        image_dir = tinfoil.config_data.getVar("DEPLOY_DIR_IMAGE")
        return pathlib.Path(image_dir)

def find(machine):
    image_dir = get_image_directory(machine)
    # All .fvpconf configuration files
    configs = image_dir.glob("*.fvpconf")
    # Just the files
    configs = [p for p in configs if p.is_file() and not p.is_symlink()]
    if not configs:
            print(f"Cannot find any .fvpconf in {image_dir}")
            raise RuntimeError()
    # Sorted by modification time
    configs = sorted(configs, key=lambda p: p.stat().st_mtime)
    return configs[-1]


def load(config_file):
    with open(config_file) as f:
        config = json.load(f)

    # Ensure that all expected keys are present
    def sanitise(key, value):
        if key not in config or config[key] is None:
            config[key] = value
    sanitise("fvp-bindir", "")
    sanitise("exe", "")
    sanitise("parameters", {})
    sanitise("data", {})
    sanitise("applications", {})
    sanitise("terminals", {})
    sanitise("args", [])
    sanitise("consoles", {})
    sanitise("env", {})

    if not config["exe"]:
        raise ValueError("Required value FVP_EXE not set in machine configuration")

    return config
