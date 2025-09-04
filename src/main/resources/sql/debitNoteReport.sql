SELECT
    d.id,
    d.file_name,
    d.created_by,
    changed_by,
    d.status,
    document_type,
    dt.comment,
    TO_CHAR(d.date_created, 'YYYY-MM-DD') AS created_date,
    TO_CHAR(dt.changed_at, 'YYYY-MM-DD') AS changed_date,
    EXTRACT(DAY FROM (dt.changed_at - d.date_created)) AS age_in_days
FROM document_transactions dt
         LEFT JOIN public.documents d ON d.id = dt.document_id
WHERE dt.changed_at = (
    SELECT MAX(changed_at)
    FROM document_transactions dt2
    WHERE dt2.document_id = dt.document_id
)
  AND (:userName IS NULL OR changed_by = :userName)
  AND (:startDate IS NULL OR d.date_created >= to_timestamp(:startDate, 'YYYY-MM-DD'))
  AND (:endDate IS NULL OR d.date_created < to_timestamp(:endDate, 'YYYY-MM-DD') + INTERVAL '1 day');


