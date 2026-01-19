# This code adds/deletes the iptable rule based on received http post request params
from flask import Flask, request, abort
import os
import subprocess
import re
iptable_rule_dict = {}
iptbl_idx = 1

# write the corresponding fwl_id to /dev/chardev file
def del_fwl_rule(fwl_id: int):

    device_file = "/dev/chardev"

    try:
        fwl_id_int = int(fwl_id)
    except ValueError:
        print("Error: integer argument required for fwl_id")
        sys.exit(1)

    fwl_id_str = str(fwl_id_int)

    # Open the device file
    device_fd = os.open(device_file, os.O_WRONLY)

    # Write the packed binary string to the device file
    ret = os.write(device_fd, fwl_id_str.encode())

    print('Sent fwl_id to kernel daemon')

    # Close the device file
    os.close(device_fd)
    return 1


def is_valid_ipv4(ip: str) -> bool:
    pattern = r"^(\d{1,3}\.){3}\d{1,3}$"
    if re.match(pattern, ip):
        octets = ip.split('.')
        return all(0 <= int(octet) <= 255 for octet in octets)
    return False


def is_valid_protocol(protocol: str) -> bool:
    protocols = {'udp', 'tcp', 'icmp', 'http', 'https', 'ftp'}
    return protocol.lower() in protocols


def is_valid_port(port: str) -> bool:
    try:
        port = int(port)
        if 0 <= port <= 65535:
            return True
        else:
            return False
    except ValueError:
        return False


def is_valid_action(action: str) -> bool:
    actions = {'ACCEPT', 'DROP'}
    return action.upper() in actions

# check the validity of all input parameters
def check_data_validity(dst_ip: str, src_ip: str, proto: str, dst_port: str, src_port: str, action: str):
    match_flag = 0
    if dst_ip and is_valid_ipv4(dst_ip):
        match_flag = 1
    if src_ip and is_valid_ipv4(src_ip):
        match_flag = 1
    if proto and is_valid_protocol(proto):
        match_flag = 1
    if dst_port and is_valid_port(dst_port):
        match_flag = 1
    if src_port and is_valid_port(src_port):
        match_flag = 1
    if action and is_valid_action(action):
        match_flag += 1
    return match_flag


app = Flask(__name__)
# curl -X POST -H "Content-Type: application/json" -d '{"dst_ip": "10.10.0.10", "proto": "tcp", "dst_port": "9000", "action":"ACCEPT"}' http://10.10.0.10:8000/add_iptables_rule
# this api proceses received post request and does the corresonding add_iptable_rule operation
@app.route('/add_iptables_rule', methods=['POST'])
def add_iptables_rule():
    global iptbl_idx
    # Get the input param from request data
    print("_FD_RCVD_POST_")
    try:
        data = request.get_json()  # Get the JSON data from the request
    except:
        return 'Invalid JSON data'

    try:
        dst_ip = data.get('dst_ip', 0)
        src_ip = data.get('src_ip', 0)
        proto = data.get('proto', 0)
        dst_port = data.get('dst_port', 0)
        src_port = data.get('src_port', 0)
        action = data.get('action', 0)
    except:
        return 'Error in extracting input data'
    print(data)
    if (check_data_validity(dst_ip, src_ip, proto, dst_port, src_port, action) == 2):
        try:
            # Define the command to add the iptables rule
            init_mark = 0x02200000
            # Modify fwl_id field(0th and 1st byte)
            mark_data = (init_mark & 0xFFFF0000) | (iptbl_idx & 0xFFFF)
            cmd1 = ['ip', 'netns', 'exec', 'defeip', 'iptables',
                    '-t', 'mangle', '-I', 'PREROUTING', '1', '-i', 'eip0']
            cmd2 = ['ip', 'netns', 'exec', 'defeip', 'iptables',
                    '-t', 'mangle', '-I', 'POSTROUTING', '1']
            if(dst_ip):
                cmd1.extend(['-d', dst_ip])
                cmd2.extend(['-d', dst_ip])
            if(src_ip):
                cmd1.extend(['-s', src_ip])
                cmd2.extend(['-s', src_ip])
            if(proto):
                cmd1.extend(['-p', proto])
                cmd2.extend(['-p', proto])
            if(dst_port):
                cmd1.extend(['--dport', dst_port])
                cmd2.extend(['--dport', dst_port])
            if(src_port):
                cmd1.extend(['--sport', src_port])
                cmd2.extend(['--sport', src_port])
            if(action == "ACCEPT"):
                pkt_res = 1
            if(action == "DENY"):
                pkt_res = 0
            # Modify 26th and 27th bit of mark_data i.e pkt_res field(valid val=>0(deny)/1(allow))
            mask = (1 << 26)
            mark_data = ((mark_data & ~mask) | (pkt_res << 26))
            cmd1.extend(['-j', 'DIVERT'])
            cmd2.extend(['-j', 'MARK', '--set-mark', hex(mark_data)])

            cmd_data = [cmd1, cmd2]
            # Run the command using subprocess
            print("cmd1")
            print(cmd1)
            print("cmd2")
            print(cmd2)
            subprocess.run(cmd1, check=True)
            subprocess.run(cmd2, check=True)
            iptable_rule_dict[iptbl_idx] = cmd_data
            iptbl_idx += 1
            print(iptable_rule_dict)
            print("_FD_ADDED_IPTBL_RULE_")
            return 'Iptables rule added successfully'
        except subprocess.CalledProcessError as e:
            return f'Error adding iptables rule: {e}'
    else:
        return 'Failure: need to pass two compulsary arguments as part of JSON data(1.dst_ip/src_ip/proto/dst_port/src_port, 2.action)'

# curl -X POST -H "Content-Type: application/json" -d '{"iprule_idx": "1"}' http://10.10.0.10:8000/del_iptables_rule
# this api proceses received post request and does the corresonding del_iptable_rule operation
@app.route('/del_iptables_rule', methods=['POST'])
def del_iptables_rule():
    print("_FD_RCVD_POST_DEL")
    try:
        data = request.get_json()  # Get the JSON data from the request
    except:
        return 'Invalid JSON data'

    try:
        # Extract needed details from the request data
        iprule_idx = int(data.get('iprule_idx', 0))
    except:
        return 'Error in extracting input data'

    if iprule_idx not in iptable_rule_dict:
        return 'iprule_idx does not exist in dict'

    # Define the command to del the iptables rule
    op_type = '-D'
    cmd1 = iptable_rule_dict[iprule_idx][0]
    cmd2 = iptable_rule_dict[iprule_idx][1]
    del cmd2[9]
    del cmd1[9]
    cmd1[7] = op_type
    cmd2[7] = op_type
    print("_FD_CDATA_")
    print(cmd1)
    print(cmd2)

    # Run the command using subprocess
    try:
        subprocess.run(cmd1, check=True)
        subprocess.run(cmd2, check=True)
        del iptable_rule_dict[iprule_idx]
        del_fwl_rule(iprule_idx)
        print("_FD_DEL_IPTBL_RULE_")
        print(iptable_rule_dict)
        return 'iptables rule deleted successfully'
    except subprocess.CalledProcessError as e:
        return f'Error deleting iptables rule: {e}'

if __name__ == '__main__':
    app.run(host="10.10.0.10", port=8000)
