import React from "react";
import map from "lodash.map";

export default function DocumentView(props) {
  const opennlp = props.selectedDocument.opennlp
    ? props.selectedDocument.opennlp
    : {};

  return (
    <div className="col col-12 px3">
      <h3>Document Content</h3>
      <p>{props.selectedDocument ? props.selectedDocument.doc.content : ""}</p>
      <h3>OpenNLP Results</h3>
      <ul>
        {map(opennlp, (entityList, entityType) => {
          return map(entityList, (entity, index) => {
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
