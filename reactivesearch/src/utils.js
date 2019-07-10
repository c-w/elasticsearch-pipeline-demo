import reduce from "lodash.reduce";

export function getUrlParams(urlParam) {
  var search = window.location.search.slice(1).split("&");
  var paramDict = reduce(
    search,
    (obj, param) => {
      var key, value;
      [key, value] = param.split("=");
      obj[key] = value;
      return obj;
    },
    {}
  );
  return paramDict[urlParam];
}
