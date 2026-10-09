export function LoadingState({ label = 'Loading' }) {
  return <div className="state-box">{label}...</div>;
}

export function ErrorState({ message }) {
  return <div className="state-box error">{message}</div>;
}

export function EmptyState({ title = 'Nothing here yet', detail = 'New items will appear here when available.' }) {
  return (
    <div className="state-box">
      <strong>{title}</strong>
      <span>{detail}</span>
    </div>
  );
}

