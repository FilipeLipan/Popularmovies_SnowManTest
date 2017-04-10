# Popularmovies
An android app that consumes the [**moviedb api**](https://www.themoviedb.org/documentation/api), showing a grid of popular and top rated movies

# Installation
To get this app up and running you will need a moviedb ``api_key`` you can get one [**here**](https://www.themoviedb.org/documentation/api)
* If you don’t already have an account, you will need to create one in order to request an API Key.
In your request for a key, state that your usage will be for educational/non-commercial use. You will also need to provide some personal information to complete the request. Once you submit your request, you should receive your key via email shortly after.

After getting your ``api_key`` inside the class ``MoviesDbService`` there is a TODO asking for your api ``api_key``

```
    //TODO: INSERT YOUR API_KEY HERE
    String API_KEY = ApiKey.API_KEY;
```
You can just replace ``ApiKey.API_KEY`` or create a class ``ApiKey`` to hold your ``api_key``
My Api class for reference:
```
public class ApiKey {
    public static String API_KEY = "yourApiKeyHere";
}
```
