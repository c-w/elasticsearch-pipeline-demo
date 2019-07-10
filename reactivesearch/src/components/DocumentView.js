import React from "react";

function EntityList(props) {
  const entities = Object.entries(props.entities);

  if (!entities.length) {
    return null;
  }

  return (
    <div>
      <h3>{props.header}</h3>
      <ul>
        {entities.map(([entityType, entityList]) =>
          (Array.isArray(entityList) ? entityList : [entityList]).map((entity, index) =>
            <li key={index}>
              <b>{entityType}</b>: {entity}
            </li>
          )
        )}
      </ul>
    </div>
  );
}

export default function DocumentView(props) {
  const opennlp = props.selectedDocument.opennlp || {};
  const textanalytics = props.selectedDocument.textanalytics || {};

  return (
    <div className="col col-12 px3">
      <h3>Document Content</h3>
      <p>{props.selectedDocument ? props.selectedDocument.doc.content : ""}</p>
      <EntityList header="OpenNLP Results" entities={opennlp} />
      <EntityList header="Text Analytics Results" entities={textanalytics} />
    </div>
  );
}
