import React from "react";

function DocumentView(props) {
  return (
    <div className="col col-12 px3">
      <h3>Document Content</h3>
      <p>{props.selectedDocument}</p>
    </div>
  );
}
export default DocumentView;
