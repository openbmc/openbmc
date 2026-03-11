#
# Copyright (C) 2012 Robert Yang
#
# SPDX-License-Identifier: GPL-2.0-only
#

import os, logging, re
import bb
logger = logging.getLogger("BitBake.Monitor")

def printErr(info):
    logger.error("%s\n       Disk space monitor will NOT be enabled" % info)

def convertGMK(unit):

    """ Convert the space unit G, M, K, the unit is case-insensitive """

    unitG = re.match(r'([1-9][0-9]*)[gG]\s?$', unit)
    if unitG:
        return int(unitG.group(1)) * (1024 ** 3)
    unitM = re.match(r'([1-9][0-9]*)[mM]\s?$', unit)
    if unitM:
        return int(unitM.group(1)) * (1024 ** 2)
    unitK = re.match(r'([1-9][0-9]*)[kK]\s?$', unit)
    if unitK:
        return int(unitK.group(1)) * 1024
    unitN = re.match(r'([1-9][0-9]*)\s?$', unit)
    if unitN:
        return int(unitN.group(1))
    else:
        return None

def getMountedDev(path):

    """ Get the device mounted at the path, uses /proc/mounts """

    # Get the mount point of the filesystem containing path
    # st_dev is the ID of device containing file
    parentDev = os.stat(path).st_dev
    currentDev = parentDev
    # When the current directory's device is different from the
    # parent's, then the current directory is a mount point
    while parentDev == currentDev:
        mountPoint = path
        # Use dirname to get the parent's directory
        path = os.path.dirname(path)
        # Reach the "/"
        if path == mountPoint:
            break
        parentDev= os.stat(path).st_dev

    try:
        with open("/proc/mounts", "r") as ifp:
            for line in ifp:
                procLines = line.rstrip('\n').split()
                if procLines[1] == mountPoint:
                    return procLines[0]
    except EnvironmentError:
        pass
    return None

def getDiskData(BBDirs):

    """Prepare disk data for disk space monitor"""

    # Save the device IDs, need the ID to be unique (the dictionary's key is
    # unique), so that when more than one directory is located on the same
    # device, we just monitor it once
    devDict = {}
    for pathSpaceInode in BBDirs.split():
        # The input format is: "dir,space,inode", dir is a must, space
        # and inode are optional
        pathSpaceInodeRe = re.match(r'([^,]*),([^,]*),([^,]*),?(.*)', pathSpaceInode)
        if not pathSpaceInodeRe:
            printErr("Invalid value in BB_DISKMON_DIRS: %s" % pathSpaceInode)
            return None

        action = pathSpaceInodeRe.group(1)
        if action == "ABORT":
            # Emit a deprecation warning
            logger.warnonce("The BB_DISKMON_DIRS \"ABORT\" action has been renamed to \"HALT\", update configuration")
            action = "HALT"

        if action not in ("HALT", "STOPTASKS", "WARN"):
            printErr("Unknown disk space monitor action: %s" % action)
            return None

        path = os.path.realpath(pathSpaceInodeRe.group(2))
        if not path:
            printErr("Invalid path value in BB_DISKMON_DIRS: %s" % pathSpaceInode)
            return None

        # The disk space or inode is optional, but it should have a correct
        # value once it is specified
        minSpace = pathSpaceInodeRe.group(3)
        if minSpace:
            minSpace = convertGMK(minSpace)
            if not minSpace:
                printErr("Invalid disk space value in BB_DISKMON_DIRS: %s" % pathSpaceInodeRe.group(3))
                return None
        else:
            # None means that it is not specified
            minSpace = None

        minInode = pathSpaceInodeRe.group(4)
        if minInode:
            minInode = convertGMK(minInode)
            if not minInode:
                printErr("Invalid inode value in BB_DISKMON_DIRS: %s" % pathSpaceInodeRe.group(4))
                return None
        else:
            # None means that it is not specified
            minInode = None

        if minSpace is None and minInode is None:
            printErr("No disk space or inode value in found BB_DISKMON_DIRS: %s" % pathSpaceInode)
            return None
        # mkdir for the directory since it may not exist, for example the
        # DL_DIR may not exist at the very beginning
        if not os.path.exists(path):
            bb.utils.mkdirhier(path)
        dev = getMountedDev(path)
        # Use path/action as the key
        devDict[(path, action)] = [dev, minSpace, minInode]

    return devDict

def getInterval(configuration):

    """ Get the disk space interval """

    # The default value is 50M and 5K.
    spaceDefault = 50 * 1024 * 1024
    inodeDefault = 5 * 1024

    interval = configuration.getVar("BB_DISKMON_WARNINTERVAL")
    if not interval:
        return spaceDefault, inodeDefault
    else:
        # The disk space or inode interval is optional, but it should
        # have a correct value once it is specified
        intervalRe = re.match(r'([^,]*),?\s*(.*)', interval)
        if intervalRe:
            intervalSpace = intervalRe.group(1)
            if intervalSpace:
                intervalSpace = convertGMK(intervalSpace)
                if not intervalSpace:
                    printErr("Invalid disk space interval value in BB_DISKMON_WARNINTERVAL: %s" % intervalRe.group(1))
                    return None, None
            else:
                intervalSpace = spaceDefault
            intervalInode = intervalRe.group(2)
            if intervalInode:
                intervalInode = convertGMK(intervalInode)
                if not intervalInode:
                    printErr("Invalid disk inode interval value in BB_DISKMON_WARNINTERVAL: %s" % intervalRe.group(2))
                    return None, None
            else:
                intervalInode = inodeDefault
            return intervalSpace, intervalInode
        else:
            printErr("Invalid interval value in BB_DISKMON_WARNINTERVAL: %s" % interval)
            return None, None

class diskMonitor:

    """Prepare the disk space monitor data"""

    def __init__(self, configuration):

        self.enableMonitor = False
        self.configuration = configuration

        BBDirs = configuration.getVar("BB_DISKMON_DIRS") or None
        if BBDirs:
            self.devDict = getDiskData(BBDirs)
            if self.devDict:
                self.spaceInterval, self.inodeInterval = getInterval(configuration)
                if self.spaceInterval and self.inodeInterval:
                    self.enableMonitor = True
                    # These are for saving the previous disk free space and inode, we
                    # use them to avoid printing too many warning messages
                    self.preFreeS = {}
                    self.preFreeI = {}
                    # This is for STOPTASKS and HALT, to avoid printing the message
                    # repeatedly while waiting for the tasks to finish
                    self.checked = {}
                    for k in self.devDict:
                        self.preFreeS[k] = 0
                        self.preFreeI[k] = 0
                        self.checked[k] = False
                    if self.spaceInterval is None and self.inodeInterval is None:
                        self.enableMonitor = False

    def check(self, rq):

        """ Take action for the monitor """

        if self.enableMonitor:
            diskUsage = {}
            for k, attributes in self.devDict.items():
                path, action = k
                dev, minSpace, minInode = attributes

                st = os.statvfs(path)

                # The available free space, integer number
                freeSpace = st.f_bavail * st.f_frsize

                # Send all relevant information in the event.
                freeSpaceRoot = st.f_bfree * st.f_frsize
                totalSpace = st.f_blocks * st.f_frsize
                diskUsage[dev] = bb.event.DiskUsageSample(freeSpace, freeSpaceRoot, totalSpace)

                if minSpace and freeSpace < minSpace:
                    # Always show warning, the self.checked would always be False if the action is WARN
                    if self.preFreeS[k] == 0 or self.preFreeS[k] - freeSpace > self.spaceInterval and not self.checked[k]:
                        logger.warning("The free space of %s (%s) is running low (%.3fGB left)" % \
                                (path, dev, freeSpace / 1024 / 1024 / 1024.0))
                        self.preFreeS[k] = freeSpace

                    if action == "STOPTASKS" and not self.checked[k]:
                        logger.error("No new tasks can be executed since the disk space monitor action is \"STOPTASKS\"!")
                        self.checked[k] = True
                        rq.finish_runqueue(False)
                        bb.event.fire(bb.event.DiskFull(dev, 'disk', freeSpace, path), self.configuration)
                    elif action == "HALT" and not self.checked[k]:
                        logger.error("Immediately halt since the disk space monitor action is \"HALT\"!")
                        self.checked[k] = True
                        rq.finish_runqueue(True)
                        bb.event.fire(bb.event.DiskFull(dev, 'disk', freeSpace, path), self.configuration)

                # The free inodes, integer number
                freeInode = st.f_favail

                if minInode and freeInode < minInode:
                    # Some filesystems use dynamic inodes so can't run out.
                    # This is reported by the inode count being 0 (btrfs) or the free
                    # inode count being -1 (cephfs).
                    if st.f_files == 0 or st.f_favail == -1:
                        self.devDict[k][2] = None
                        continue
                    # Always show warning, the self.checked would always be False if the action is WARN
                    if self.preFreeI[k] == 0 or self.preFreeI[k] - freeInode > self.inodeInterval and not self.checked[k]:
                        logger.warning("The free inode of %s (%s) is running low (%.3fK left)" % \
                                (path, dev, freeInode / 1024.0))
                        self.preFreeI[k] = freeInode

                    if action  == "STOPTASKS" and not self.checked[k]:
                        logger.error("No new tasks can be executed since the disk space monitor action is \"STOPTASKS\"!")
                        self.checked[k] = True
                        rq.finish_runqueue(False)
                        bb.event.fire(bb.event.DiskFull(dev, 'inode', freeInode, path), self.configuration)
                    elif action  == "HALT" and not self.checked[k]:
                        logger.error("Immediately halt since the disk space monitor action is \"HALT\"!")
                        self.checked[k] = True
                        rq.finish_runqueue(True)
                        bb.event.fire(bb.event.DiskFull(dev, 'inode', freeInode, path), self.configuration)

            bb.event.fire(bb.event.MonitorDiskEvent(diskUsage), self.configuration)
        return
