interface Props {
  status: boolean;
}

const ConnectionStatus = ({ status }: Props) => {
  return (
    <>
      {status ? (
        <>
          <div className="badge badge-success">Connected</div>
        </>
      ) : (
        <>
          <div className="badge badge-neutral">Offline</div>
        </>
      )}
    </>
  );
};

export default ConnectionStatus;
