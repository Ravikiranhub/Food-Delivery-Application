export default function MetricGrid({ data }) {
  const counts = Object.entries(data?.counts || {});
  const money = Object.entries(data?.money || {});

  return (
    <div className="metric-grid">
      {[...counts, ...money].map(([key, value]) => (
        <div className="metric-card" key={key}>
          <span>{labelize(key)}</span>
          <strong>{formatValue(value)}</strong>
        </div>
      ))}
    </div>
  );
}

function labelize(value) {
  return value.replace(/([A-Z])/g, ' $1').replace(/^./, (char) => char.toUpperCase());
}

function formatValue(value) {
  if (typeof value === 'number') {
    return value.toLocaleString();
  }
  return value;
}

