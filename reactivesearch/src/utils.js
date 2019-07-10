export function getUrlParams(urlParam) {
  const search = window.location.search.slice(1).split("&");
  const paramDict = search.reduce(
    (obj, param) => {
      const [key, value] = param.split("=");
      obj[key] = value;
      return obj;
    },
    {}
  );
  return paramDict[urlParam];
}
