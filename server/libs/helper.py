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


def init():
    configInstance = configparser.ConfigParser()

    configInstance["debug"] = {}
    configInstance["debug"]["type"] = "action"
    configInstance["debug"]["cmd"] = "echo debug"

    configInstance["debug2"] = {}
    configInstance["debug2"]["type"] = "status"
    configInstance["debug2"]["cmd"] = "echo 123"

    configInstance["debug3"] = {}
    configInstance["debug3"]["type"] = "setter"  
    configInstance["debug3"]["cmd"] = "echo"

    with open('config.ini', 'w') as configfile:
        configInstance.write(configfile)
    print('done')
