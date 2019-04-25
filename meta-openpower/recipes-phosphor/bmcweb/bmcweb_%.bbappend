SUMMARY = "Modifications to support phosphor-inventory-manager"

# No way to link sensors to a chassis for Redfish in phosphor-inventory-manager.
# Assume just one chassis via BMCWEB_ENABLE_REDFISH_ONE_CHASSIS option. All
# IBM systems currently have a single chassis. This is a short-term solution.
EXTRA_OECMAKE += "-DBMCWEB_ENABLE_REDFISH_ONE_CHASSIS=ON"
