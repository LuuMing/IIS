import configparser
import json
import sys
import os
def check_exist():
    """检测所需文件是否存在"""
    if os.path.isfile('config.ini'):
        return True
    else:
        return False

def get_config():
    configInstance = configparser.ConfigParser()
    if not check_exist():
        print('run init first')
        exit()
    configInstance.read('config.ini')
    return configInstance
    pass

def get_config_json():
    pass

def init():
    configInstance = configparser.ConfigParser()
    configInstance["target"] = {}
    configInstance["target"]["type"] = "status"
    configInstance["target"]["source"]  = "str"
    configInstance["target"]["cmd"] = sys.platform
    configInstance["target"]["ret_type"] = "string"

    configInstance["debug"] = {}
    configInstance["debug"]["type"] = "action"
    configInstance["debug"]["source"] = "os"
    configInstance["debug"]["cmd"] = "echo debug"

    configInstance["debug2"] = {}
    configInstance["debug2"]["type"] = "status"
    configInstance["debug2"]["source"] = "os"
    configInstance["debug2"]["cmd"] = "echo 123"
    configInstance["debug2"]["ret_type"] = "int"

    with open('config.ini', 'w') as configfile:
        configInstance.write(configfile)
    print('done')
