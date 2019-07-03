import React from "react";
import _ from "lodash";

function DocumentView(props) {
  const opennlp = props.selectedDocument.opennlp
    ? props.selectedDocument.opennlp
    : {};

  return (
    <div className="col col-12 px3">
      <h3>Document Content</h3>
      <p>{props.selectedDocument ? props.selectedDocument.doc.content : ""}</p>
      <h3>OpenNLP Results</h3>
      <ul>
        {_.map(opennlp, function(entityList, entityType) {
          return _.map(entityList, function(entity, index) {
            return (
              <li key={index}>
                <b>{entityType}</b>: {entity}
              </li>
            );
          });
        })}
      </ul>
    </div>
  );
}
export default DocumentView;
