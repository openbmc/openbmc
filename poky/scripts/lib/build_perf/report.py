#
# Copyright (c) 2017, Intel Corporation.
#
# SPDX-License-Identifier: GPL-2.0-only
#
"""Handling of build perf test reports"""
from collections import OrderedDict, namedtuple
from collections.abc import Mapping
from datetime import datetime, timezone
from numbers import Number
from statistics import mean, stdev, variance


AggregateTestData = namedtuple('AggregateTestData', ['metadata', 'results'])


def isofmt_to_timestamp(string):
    """Convert timestamp string in ISO 8601 format into unix timestamp"""
    if '.' in string:
        dt = datetime.strptime(string, '%Y-%m-%dT%H:%M:%S.%f')
    else:
        dt = datetime.strptime(string, '%Y-%m-%dT%H:%M:%S')
    return dt.replace(tzinfo=timezone.utc).timestamp()


def metadata_xml_to_json(elem):
    """Convert metadata xml into JSON format"""
    assert elem.tag == 'metadata', "Invalid metadata file format"

    def _xml_to_json(elem):
        """Convert xml element to JSON object"""
        out = OrderedDict()
        for child in elem.getchildren():
            key = child.attrib.get('name', child.tag)
            if len(child):
                out[key] = _xml_to_json(child)
            else:
                out[key] = child.text
        return out
    return _xml_to_json(elem)


def results_xml_to_json(elem):
    """Convert results xml into JSON format"""
    rusage_fields = ('ru_utime', 'ru_stime', 'ru_maxrss', 'ru_minflt',
                     'ru_majflt', 'ru_inblock', 'ru_oublock', 'ru_nvcsw',
                     'ru_nivcsw')
    iostat_fields = ('rchar', 'wchar', 'syscr', 'syscw', 'read_bytes',
                     'write_bytes', 'cancelled_write_bytes')

    def _read_measurement(elem):
        """Convert measurement to JSON"""
        data = OrderedDict()
        data['type'] = elem.tag
        data['name'] = elem.attrib['name']
        data['legend'] = elem.attrib['legend']
        values = OrderedDict()

        # SYSRES measurement
        if elem.tag == 'sysres':
            for subel in elem:
                if subel.tag == 'time':
                    values['start_time'] = isofmt_to_timestamp(subel.attrib['timestamp'])
                    values['elapsed_time'] = float(subel.text)
                elif subel.tag == 'rusage':
                    rusage = OrderedDict()
                    for field in rusage_fields:
                        if 'time' in field:
                            rusage[field] = float(subel.attrib[field])
                        else:
                            rusage[field] = int(subel.attrib[field])
                    values['rusage'] = rusage
                elif subel.tag == 'iostat':
                    values['iostat'] = OrderedDict([(f, int(subel.attrib[f]))
                        for f in iostat_fields])
                elif subel.tag == 'buildstats_file':
                    values['buildstats_file'] = subel.text
                else:
                    raise TypeError("Unknown sysres value element '{}'".format(subel.tag))
        # DISKUSAGE measurement
        elif elem.tag == 'diskusage':
            values['size'] = int(elem.find('size').text)
        else:
            raise Exception("Unknown measurement tag '{}'".format(elem.tag))
        data['values'] = values
        return data

    def _read_testcase(elem):
        """Convert testcase into JSON"""
        assert elem.tag == 'testcase', "Expecting 'testcase' element instead of {}".format(elem.tag)

        data = OrderedDict()
        data['name'] = elem.attrib['name']
        data['description'] = elem.attrib['description']
        data['status'] = 'SUCCESS'
        data['start_time'] = isofmt_to_timestamp(elem.attrib['timestamp'])
        data['elapsed_time'] = float(elem.attrib['time'])
        measurements = OrderedDict()

        for subel in elem.getchildren():
            if subel.tag == 'error' or subel.tag == 'failure':
                data['status'] = subel.tag.upper()
                data['message'] = subel.attrib['message']
                data['err_type'] = subel.attrib['type']
                data['err_output'] = subel.text
            elif subel.tag == 'skipped':
                data['status'] = 'SKIPPED'
                data['message'] = subel.text
            else:
                measurements[subel.attrib['name']] = _read_measurement(subel)
        data['measurements'] = measurements
        return data

    def _read_testsuite(elem):
        """Convert suite to JSON"""
        assert elem.tag == 'testsuite', \
                "Expecting 'testsuite' element instead of {}".format(elem.tag)

        data = OrderedDict()
        if 'hostname' in elem.attrib:
            data['tester_host'] = elem.attrib['hostname']
        data['start_time'] = isofmt_to_timestamp(elem.attrib['timestamp'])
        data['elapsed_time'] = float(elem.attrib['time'])
        tests = OrderedDict()

        for case in elem.getchildren():
            tests[case.attrib['name']] = _read_testcase(case)
        data['tests'] = tests
        return data

    # Main function
    assert elem.tag == 'testsuites', "Invalid test report format"
    assert len(elem) == 1, "Too many testsuites"

    return _read_testsuite(elem.getchildren()[0])


def aggregate_metadata(metadata):
    """Aggregate metadata into one, basically a sanity check"""
    mutable_keys = ('pretty_name', 'version_id')

    def aggregate_obj(aggregate, obj, assert_str=True):
        """Aggregate objects together"""
        assert type(aggregate) is type(obj), \
                "Type mismatch: {} != {}".format(type(aggregate), type(obj))
        if isinstance(obj, Mapping):
            assert set(aggregate.keys()) == set(obj.keys())
            for key, val in obj.items():
                aggregate_obj(aggregate[key], val, key not in mutable_keys)
        elif isinstance(obj, list):
            assert len(aggregate) == len(obj)
            for i, val in enumerate(obj):
                aggregate_obj(aggregate[i], val)
        elif not isinstance(obj, str) or (isinstance(obj, str) and assert_str):
            assert aggregate == obj, "Data mismatch {} != {}".format(aggregate, obj)

    if not metadata:
        return {}

    # Do the aggregation
    aggregate = metadata[0].copy()
    for testrun in metadata[1:]:
        aggregate_obj(aggregate, testrun)
    aggregate['testrun_count'] = len(metadata)
    return aggregate


def aggregate_data(data):
    """Aggregate multiple test results JSON structures into one"""

    mutable_keys = ('status', 'message', 'err_type', 'err_output')

    class SampleList(list):
        """Container for numerical samples"""
        pass

    def new_aggregate_obj(obj):
        """Create new object for aggregate"""
        if isinstance(obj, Number):
            new_obj = SampleList()
            new_obj.append(obj)
        elif isinstance(obj, str):
            new_obj = obj
        else:
            # Lists and and dicts are kept as is
            new_obj = obj.__class__()
            aggregate_obj(new_obj, obj)
        return new_obj

    def aggregate_obj(aggregate, obj, assert_str=True):
        """Recursive "aggregation" of JSON objects"""
        if isinstance(obj, Number):
            assert isinstance(aggregate, SampleList)
            aggregate.append(obj)
            return

        assert type(aggregate) == type(obj), \
                "Type mismatch: {} != {}".format(type(aggregate), type(obj))
        if isinstance(obj, Mapping):
            for key, val in obj.items():
                if not key in aggregate:
                    aggregate[key] = new_aggregate_obj(val)
                else:
                    aggregate_obj(aggregate[key], val, key not in mutable_keys)
        elif isinstance(obj, list):
            for i, val in enumerate(obj):
                if i >= len(aggregate):
                    aggregate[key] = new_aggregate_obj(val)
                else:
                    aggregate_obj(aggregate[i], val)
        elif isinstance(obj, str):
            # Sanity check for data
            if assert_str:
                assert aggregate == obj, "Data mismatch {} != {}".format(aggregate, obj)
        else:
            raise Exception("BUG: unable to aggregate '{}' ({})".format(type(obj), str(obj)))

    if not data:
        return {}

    # Do the aggregation
    aggregate = data[0].__class__()
    for testrun in data:
        aggregate_obj(aggregate, testrun)
    return aggregate


class MeasurementVal(float):
    """Base class representing measurement values"""
    gv_data_type = 'number'

    def gv_value(self):
        """Value formatting for visualization"""
        if self != self:
            return "null"
        else:
            return self


class TimeVal(MeasurementVal):
    """Class representing time values"""
    quantity = 'time'
    gv_title = 'elapsed time'
    gv_data_type = 'timeofday'

    def hms(self):
        """Split time into hours, minutes and seconeds"""
        hhh = int(abs(self) / 3600)
        mmm = int((abs(self) % 3600) / 60)
        sss = abs(self) % 60
        return hhh, mmm, sss

    def __str__(self):
        if self != self:
            return "nan"
        hh, mm, ss = self.hms()
        sign = '-' if self < 0 else ''
        if hh > 0:
            return '{}{:d}:{:02d}:{:02.0f}'.format(sign, hh, mm, ss)
        elif mm > 0:
            return '{}{:d}:{:04.1f}'.format(sign, mm, ss)
        elif ss > 1:
            return '{}{:.1f} s'.format(sign, ss)
        else:
            return '{}{:.2f} s'.format(sign, ss)

    def gv_value(self):
        """Value formatting for visualization"""
        if self != self:
            return "null"
        hh, mm, ss = self.hms()
        return [hh, mm, int(ss), int(ss*1000) % 1000]


class SizeVal(MeasurementVal):
    """Class representing time values"""
    quantity = 'size'
    gv_title = 'size in MiB'
    gv_data_type = 'number'

    def __str__(self):
        if self != self:
            return "nan"
        if abs(self) < 1024:
            return '{:.1f} kiB'.format(self)
        elif abs(self) < 1048576:
            return '{:.2f} MiB'.format(self / 1024)
        else:
            return '{:.2f} GiB'.format(self / 1048576)

    def gv_value(self):
        """Value formatting for visualization"""
        if self != self:
            return "null"
        return self / 1024

def measurement_stats(meas, prefix='', time=0):
    """Get statistics of a measurement"""
    if not meas:
        return {prefix + 'sample_cnt': 0,
                prefix + 'mean': MeasurementVal('nan'),
                prefix + 'stdev': MeasurementVal('nan'),
                prefix + 'variance': MeasurementVal('nan'),
                prefix + 'min': MeasurementVal('nan'),
                prefix + 'max': MeasurementVal('nan'),
                prefix + 'minus': MeasurementVal('nan'),
                prefix + 'plus': MeasurementVal('nan')}

    stats = {'name': meas['name']}
    if meas['type'] == 'sysres':
        val_cls = TimeVal
        values = meas['values']['elapsed_time']
    elif meas['type'] == 'diskusage':
        val_cls = SizeVal
        values = meas['values']['size']
    else:
        raise Exception("Unknown measurement type '{}'".format(meas['type']))
    stats['val_cls'] = val_cls
    stats['quantity'] = val_cls.quantity
    stats[prefix + 'sample_cnt'] = len(values)

    # Add start time for both type sysres and disk usage
    start_time = time 
    mean_val = val_cls(mean(values))
    min_val = val_cls(min(values))
    max_val = val_cls(max(values))

    stats[prefix + 'mean'] = mean_val
    if len(values) > 1:
        stats[prefix + 'stdev'] = val_cls(stdev(values))
        stats[prefix + 'variance'] = val_cls(variance(values))
    else:
        stats[prefix + 'stdev'] = float('nan')
        stats[prefix + 'variance'] = float('nan')
    stats[prefix + 'min'] = min_val
    stats[prefix + 'max'] = max_val
    stats[prefix + 'minus'] = val_cls(mean_val - min_val)
    stats[prefix + 'plus'] = val_cls(max_val - mean_val)
    stats[prefix + 'start_time'] = start_time

    return stats

