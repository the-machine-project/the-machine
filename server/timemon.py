###The Machine 'Day' Calculator###
#Coded by Matthew Wolffsohn - Reddit: Xalaxis - For "The Machine" project.
import time
import json

debug=0 #Set to 1 whilst debugging.  Otherwise disable to prevent console spam.

def debugprint(text):
    global debug
    if debug == 1:
        print("DEBUG: "+text)

runseconds=0 #Resetting initial values
runminutes=0
runhours=0
rundays=0
serveroverload=False #If system should deactivate itself as the server is being overloaded with requests
devstatus=True #If the sytem is in development still or not (so it knows whether it is a 'final' release)
latestver="0.1.0" #The latest version of the system (for updating purposes)

def updatejsondata(): #Write the current states of the variables to systemtimes.json
    runtime = {
        'timeseconds' : runseconds,
        'timeminutes' : runminutes,
        'timehours' : runhours,
        'timedays' : rundays,
        'serveroverload' : serveroverload,
        'devstatus' : devstatus,
        'latestver' : latestver,
        }
    with open('systemtimes.json', 'w') as outfile:
              json.dump(runtime, outfile)

while True:
    time.sleep(1) #Every second
    runseconds=runseconds+1 #Add a second to the second variable
    debugprint("Second added")
    if runseconds == 60: #If a minute has passed
        runseconds=0 #Reset the seconds
        runminutes=runminutes+1 #And add a minute to the minute variable
        debugprint("Minute added")
    if runminutes == 60: #If an hour has passed
        runminutes=0 #Reset the minutes
        runhours=runhours+1 #And add a hour to the hour variable
        debugprint("Hours added")
    if runhours == 24: #If a day has passed
        runhours=0 #Reset the hours
        rundays=rundays+1 #And add a day to the day variable
        debugprint("Day added")
    updatejsondata() #Write the current states of the variables to systemtimes.json
