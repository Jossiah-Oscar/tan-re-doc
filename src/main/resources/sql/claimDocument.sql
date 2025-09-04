SELECT
    cd.contract_number,
    cd.claim_number,
    cd.broker_cedant,
    cd.insured,
    cd.loss_date,
    cdfs.label AS finance_status,
    cds.name   AS document_status,
    cdt.comment AS last_action_comment,
    cd.created_at
FROM claim_documents cd
         LEFT JOIN LATERAL (
    SELECT t.*
    FROM claim_doc_transaction t
    WHERE t.document_id = cd.id
    ORDER BY t.changed_at DESC
        LIMIT 1
    ) cdt ON TRUE
    LEFT JOIN claim_document_finance_status cdfs
    ON cdfs.id = cdt.to_finance_status_id
    LEFT JOIN claim_document_status cds
    ON cds.id = cd.status_id
WHERE (:startDate IS NULL OR cd.created_at >= to_date(:startDate, 'YYYY-MM-DD')::timestamp)
  AND (:endDate   IS NULL OR cd.created_at <  (to_date(:endDate,   'YYYY-MM-DD') + INTERVAL '1 day')::timestamp)

