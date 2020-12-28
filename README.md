# map-point-of-interst-backend

This is a soap api where the user can give as input the coordinates of some location and the api will search and return the most close coordinates that are found. 
The api searches a database that keeps geospatial data. Each location contains also a counter.
After each search the api increases the counter for the found location in the db so that we can measure how many times a specific location has been returned as the closest location.
