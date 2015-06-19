###The Machine 'Day' Calculator###
#Coded by Matthew Wolffsohn - Reddit: Xalaxis - For "The Machine" project.
import time
import json

runseconds=0
runminutes=0
runhours=0
rundays=0
onlinestate=True

def updatejsondata():
    runtime = {
        'timeseconds' : runseconds
        'timeminutes' : runminutes
        'timehours' : runhours
        'timedays' : rundays
        'offline' : onlinestate
        }

While True:
    time.sleep(1)
    runseconds=runseconds+1
    if runseconds == 60:
        runseconds=0
        runminutes=runminutes+1
    if runminutes == 60:
        runminutes=0
        runhours=runhours+1
    if runhours == 24:
        runhours=0
        rundays=rundays+1
    updatejsondata()
    with open('systemtimes.json', 'w') as outfile:
        json.dump(runtime, outfile)
